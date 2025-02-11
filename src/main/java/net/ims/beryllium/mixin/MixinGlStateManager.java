package net.ims.beryllium.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import net.ims.beryllium.GLDebug;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.IntBuffer;
import java.util.BitSet;

@Mixin(value = GlStateManager.class, remap = false)
public abstract class MixinGlStateManager {
	@Shadow
	@Final
	private static GlStateManager.TextureState[] TEXTURES;

	@Shadow
	private static int activeTexture = 0;

	@Shadow
	public static int _getError() { return 0; }

	@Unique
	private static BitSet isDirty;

	@Unique
	private static Int2IntArrayMap framebufferMap = new Int2IntArrayMap();

	@Unique
	private static boolean isFramebufferDirty = false;

	@Unique
	private static int currentProgram;

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void setDirtyArray(CallbackInfo ci) {
		GLDebug.setupDebugMessageCallback();
		isDirty = new BitSet(TEXTURES.length);
	}

	@Overwrite
	public static void _clear(int i, boolean bl) {
		RenderSystem.assertOnRenderThreadOrInit();
		updateState();
		GL11.glClear(i);
		if (bl) {
			_getError();
		}
	}

	@Overwrite
	public static int glGenFramebuffers() {
		RenderSystem.assertOnRenderThreadOrInit();
		return ARBDirectStateAccess.glCreateFramebuffers();
	}

	@Overwrite
	public static int _genTexture() {
		RenderSystem.assertOnRenderThreadOrInit();
		return ARBDirectStateAccess.glCreateTextures(GL30C.GL_TEXTURE_2D);
	}

	@Overwrite
	public static void _genTextures(int[] is) {
		RenderSystem.assertOnRenderThreadOrInit();
		ARBDirectStateAccess.glCreateTextures(GL30C.GL_TEXTURE_2D, is);
	}


	@Inject(method = "_drawElements", at = @At("HEAD"))
	private static void checkForDirty(int i, int j, int k, long l, CallbackInfo ci) {
		updateState();
	}

	private static void updateState() {
		isDirty.stream().forEach(value -> {
			isDirty.clear(value);
			GlStateManager.TextureState state = TEXTURES[value];
			if (state.enable) {
				ARBDirectStateAccess.glBindTextureUnit(value, state.binding);
			}
		});

		if (isFramebufferDirty) {
			isFramebufferDirty = false;
			GL43C.glBindFramebuffer(GL30C.GL_FRAMEBUFFER, framebufferMap.get(GL30C.GL_FRAMEBUFFER));
		}
	}

	@Overwrite
	public static void glActiveTexture(int i) {
	}

	@Overwrite
	public static int glCheckFramebufferStatus(int i) {
		RenderSystem.assertOnRenderThreadOrInit();
		return ARBDirectStateAccess.glCheckNamedFramebufferStatus(framebufferMap.get(i), GL30C.GL_FRAMEBUFFER);
	}

	@Overwrite
	public static void _glFramebufferTexture2D(int i, int j, int k, int l, int m) {
		RenderSystem.assertOnRenderThreadOrInit();
		ARBDirectStateAccess.glNamedFramebufferTexture(framebufferMap.get(i), j, l, m);
	}


	@Overwrite
	public static void _bindTexture(int i) {
		RenderSystem.assertOnRenderThreadOrInit();
		if (i != TEXTURES[activeTexture].binding) {
			TEXTURES[activeTexture].binding = i;
			isDirty.set(activeTexture);
		}
	}


	@Overwrite
	public static void _texParameter(int i, int j, float f) {
		RenderSystem.assertOnRenderThreadOrInit();
		ARBDirectStateAccess.glTextureParameterf(getTexture(), j, f);
	}

	@Overwrite
	public static void _texParameter(int i, int j, int k) {
		RenderSystem.assertOnRenderThreadOrInit();
		ARBDirectStateAccess.glTextureParameterIi(getTexture(), j, k);
	}

	@Overwrite
	public static int _getTexLevelParameter(int i, int j, int k) {
		RenderSystem.assertInInitPhase();
		return GL30C.glGetTexLevelParameteri(i, j, k);
	}

	private static int getTexture() {
		return TEXTURES[activeTexture].binding;
	}

	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public static void _glBindFramebuffer(int i, int j) {
		int previousValue = framebufferMap.get(i);
		if (j == previousValue) return;
		framebufferMap.put(i, j);
		isFramebufferDirty = isFramebufferDirty || i == GL30C.GL_FRAMEBUFFER;
	}

	@Overwrite
	public static void _glBlitFrameBuffer(int i, int j, int k, int l, int m, int n, int o, int p, int q, int r) {
		RenderSystem.assertOnRenderThreadOrInit();
		ARBDirectStateAccess.glBlitNamedFramebuffer(framebufferMap.get(GL43C.GL_READ_FRAMEBUFFER), framebufferMap.get(GL43C.GL_DRAW_FRAMEBUFFER), i, j, k, l, m, n, o, p, q, r);
	}

	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public static void _texImage2D(int i, int j, int k, int l, int m, int n, int o, int p, @Nullable IntBuffer intBuffer) {
		RenderSystem.assertOnRenderThreadOrInit();
		GL43C.glActiveTexture(GL30C.GL_TEXTURE0 + activeTexture);
		GL43C.glBindTexture(GL20C.GL_TEXTURE_2D, getTexture());
		GL11.glTexImage2D(i, j, k, l, m, n, o, p, intBuffer);
	}

	@Overwrite
	public static void _texSubImage2D(int i, int j, int k, int l, int m, int n, int o, int p, long q) {
		RenderSystem.assertOnRenderThreadOrInit();
		ARBDirectStateAccess.glTextureSubImage2D(getTexture(), j, k, l, m, n, o, p, q);
	}

	@Overwrite
	public static void _getTexImage(int i, int j, int k, int l, long m) {
		RenderSystem.assertOnRenderThread();
		ARBDirectStateAccess.glGetTextureImage(getTexture(), i, j, k, l, m);
	}

	/**
	 * @author
	 * @reason
	 */
	@Overwrite
	public static void _glUseProgram(int i) {
		RenderSystem.assertOnRenderThread();
		if (i == 0) {
			currentProgram = 0;
			return;
		}

		if (i != currentProgram) {
			currentProgram = i;
			GL20.glUseProgram(i);
		}
	}
}
