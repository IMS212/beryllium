package net.ims.beryllium.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.*;
import org.spongepowered.asm.mixin.*;

import java.nio.IntBuffer;

@Mixin(GlStateManager.class)
public class MixinGlStateManager {
	@Shadow
	@Final
	private static GlStateManager.TextureState[] TEXTURES;

	@Shadow
	private static int activeTexture;

	@Unique
	private static Int2IntArrayMap framebufferMap = new Int2IntArrayMap();

	@Overwrite
	public static void glActiveTexture(int i) {
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
		return ARBDirectStateAccess.glGetTextureLevelParameteri(getTexture(), j, k);
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
		framebufferMap.put(i, j);
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
		// No way around this
		GL43C.glActiveTexture(activeTexture);
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
}
