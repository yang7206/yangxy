package org.yxy.pic.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.haoran.distort.BulgeFilter;
import org.haoran.distort.RippleFilter;
import org.haoran.distort.TwistFilter;
import org.haoran.distort.WaveFilter;
import org.haoran.imagefilter.AutoAdjustFilter;
import org.haoran.imagefilter.BannerFilter;
import org.haoran.imagefilter.BigBrotherFilter;
import org.haoran.imagefilter.BlackWhiteFilter;
import org.haoran.imagefilter.BlindFilter;
import org.haoran.imagefilter.BlockPrintFilter;
import org.haoran.imagefilter.BrickFilter;
import org.haoran.imagefilter.BrightContrastFilter;
import org.haoran.imagefilter.CleanGlassFilter;
import org.haoran.imagefilter.ColorQuantizeFilter;
import org.haoran.imagefilter.ColorToneFilter;
import org.haoran.imagefilter.ComicFilter;
import org.haoran.imagefilter.EdgeFilter;
import org.haoran.imagefilter.FeatherFilter;
import org.haoran.imagefilter.FillPatternFilter;
import org.haoran.imagefilter.FilmFilter;
import org.haoran.imagefilter.FocusFilter;
import org.haoran.imagefilter.GammaFilter;
import org.haoran.imagefilter.GaussianBlurFilter;
import org.haoran.imagefilter.Gradient;
import org.haoran.imagefilter.HslModifyFilter;
import org.haoran.imagefilter.IImageFilter;
import org.haoran.imagefilter.IllusionFilter;
import org.haoran.imagefilter.Image;
import org.haoran.imagefilter.InvertFilter;
import org.haoran.imagefilter.LensFlareFilter;
import org.haoran.imagefilter.LightFilter;
import org.haoran.imagefilter.LomoFilter;
import org.haoran.imagefilter.MirrorFilter;
import org.haoran.imagefilter.MistFilter;
import org.haoran.imagefilter.MonitorFilter;
import org.haoran.imagefilter.MosaicFilter;
import org.haoran.imagefilter.NeonFilter;
import org.haoran.imagefilter.NightVisionFilter;
import org.haoran.imagefilter.NoiseFilter;
import org.haoran.imagefilter.OilPaintFilter;
import org.haoran.imagefilter.OldPhotoFilter;
import org.haoran.imagefilter.PaintBorderFilter;
import org.haoran.imagefilter.PixelateFilter;
import org.haoran.imagefilter.PosterizeFilter;
import org.haoran.imagefilter.RadialDistortionFilter;
import org.haoran.imagefilter.RainBowFilter;
import org.haoran.imagefilter.RaiseFrameFilter;
import org.haoran.imagefilter.RectMatrixFilter;
import org.haoran.imagefilter.ReflectionFilter;
import org.haoran.imagefilter.ReliefFilter;
import org.haoran.imagefilter.SaturationModifyFilter;
import org.haoran.imagefilter.SceneFilter;
import org.haoran.imagefilter.SepiaFilter;
import org.haoran.imagefilter.SharpFilter;
import org.haoran.imagefilter.ShiftFilter;
import org.haoran.imagefilter.SmashColorFilter;
import org.haoran.imagefilter.SoftGlowFilter;
import org.haoran.imagefilter.SupernovaFilter;
import org.haoran.imagefilter.ThreeDGridFilter;
import org.haoran.imagefilter.ThresholdFilter;
import org.haoran.imagefilter.TileReflectionFilter;
import org.haoran.imagefilter.TintFilter;
import org.haoran.imagefilter.VideoFilter;
import org.haoran.imagefilter.VignetteFilter;
import org.haoran.imagefilter.VintageFilter;
import org.haoran.imagefilter.WaterWaveFilter;
import org.haoran.imagefilter.XRadiationFilter;
import org.haoran.imagefilter.YCBCrLinearFilter;
import org.haoran.imagefilter.ZoomBlurFilter;
import org.haoran.textures.CloudsTexture;
import org.haoran.textures.LabyrinthTexture;
import org.haoran.textures.MarbleTexture;
import org.haoran.textures.TextileTexture;
import org.haoran.textures.TexturerFilter;
import org.haoran.textures.WoodTexture;
import org.yxy.pic.R;
import org.yxy.pic.utils.BitmapManagerUtils;
import org.yxy.pic.utils.BitmapUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ImageFilterMain extends Activity {

	private ImageView imageView;
	private TextView textView;

	private Handler mHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initThread();
		mHandler = new Handler(getMainLooper());

		imageView = (ImageView) findViewById(R.id.imgfilter);
		textView = (TextView) findViewById(R.id.runtime);

		int type = getIntent().getIntExtra(REQUEST_TYPE, 0);
		switch (type) {
		case REQUEST_CODE_PICK_IMAGE:
			getImageFromAlbum();
			break;
		case REQUEST_CODE_CAPTURE_CAMEIA:
			getImageFromCamera();
			break;
		default:
			finish();
			break;
		}
		LoadImageFilter();
	}

	private ImageFilterAdapter filterAdapter;

	/**
	 * 
	 */
	@SuppressWarnings("deprecation")
	private void LoadImageFilter() {
		filterAdapter = new ImageFilterAdapter(ImageFilterMain.this);
		Gallery gallery = (Gallery) findViewById(R.id.galleryFilter);
		gallery.setAdapter(new ImageFilterAdapter(ImageFilterMain.this));
		gallery.setSelection(2);
		gallery.setAnimationDuration(3000);
		gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				performImageFilter(position);
			}
		});
	}

	private void performImageFilter(int position) {
		if (currFilterPosition == position || isFilting)
			return;

		isFilting = true;
		currFilterPosition = position;
		IImageFilter filter = (IImageFilter) filterAdapter.getItem(position);
		new processImageTask(ImageFilterMain.this, filter).execute();
	}

	public class processImageTask extends AsyncTask<Void, Void, Bitmap> {
		private IImageFilter filter;
		private Activity activity = null;

		public processImageTask(Activity activity, IImageFilter imageFilter) {
			this.filter = imageFilter;
			this.activity = activity;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			textView.setVisibility(View.VISIBLE);
			if (mCurrShowBitmap != null) {
				mCurrShowBitmap.recycle();
			}
			imageView.setImageBitmap(mCurrSrcBitmap);
		}

		public Bitmap doInBackground(Void... params) {
			Image img = null;
			try {
				img = new Image(mCurrSrcBitmap);
				if (filter != null) {
					img = filter.process(img);
					img.copyPixelsFromBuffer();
				}
				return img.getImage();
			} catch (Exception e) {
				if (img != null && img.destImage.isRecycled()) {
					img.destImage.recycle();
					img.destImage = null;
					System.gc();
				}
			} finally {
				if (img != null && img.image.isRecycled()) {
					img.image.recycle();
					img.image = null;
					System.gc();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				super.onPostExecute(result);
				showImageBitmap(result);
			}
			isFilting = false;
			textView.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BitmapManagerUtils.destory();
	}

	public class ImageFilterAdapter extends BaseAdapter {
		private class FilterInfo {
			public int filterID;
			public IImageFilter filter;

			public FilterInfo(int filterID, IImageFilter filter) {
				this.filterID = filterID;
				this.filter = filter;
			}
		}

		private Context mContext;
		private List<FilterInfo> filterArray = new ArrayList<FilterInfo>();

		public ImageFilterAdapter(Context c) {
			mContext = c;

			// 99��Ч��

			// v0.4
			// filterArray.add(new FilterInfo("", new VideoFilter(
			// VideoFilter.VIDEO_TYPE.VIDEO_STAGGERED)));
			// filterArray.add(new FilterInfo("", new VideoFilter(
			// VideoFilter.VIDEO_TYPE.VIDEO_TRIPED)));
			// filterArray.add(new FilterInfo("", new VideoFilter(
			// VideoFilter.VIDEO_TYPE.VIDEO_3X3)));
			// filterArray.add(new FilterInfo("", new VideoFilter(
			// VideoFilter.VIDEO_TYPE.VIDEO_DOTS)));
			// filterArray.add(new FilterInfo("", new TileReflectionFilter(20,
			// 8,
			// 45, (byte) 1)));
			// filterArray.add(new FilterInfo("", new TileReflectionFilter(20,
			// 8,
			// 45, (byte) 2)));
			// filterArray.add(new FilterInfo("", new FillPatternFilter(
			// getBaseContext(), R.drawable.texture1)));
			// filterArray.add(new FilterInfo("", new FillPatternFilter(
			// getBaseContext(), R.drawable.texture2)));
			// filterArray.add(new FilterInfo("", new MirrorFilter(true)));
			// filterArray.add(new FilterInfo("", new MirrorFilter(false)));
			// filterArray.add(new FilterInfo("", new YCBCrLinearFilter(
			// new YCBCrLinearFilter.Range(-0.3f, 0.3f))));
			// filterArray.add(new FilterInfo("", new YCBCrLinearFilter(
			// new YCBCrLinearFilter.Range(-0.276f, 0.163f),
			// new YCBCrLinearFilter.Range(-0.202f, 0.5f))));
			// filterArray.add(new FilterInfo("", new TexturerFilter(
			// new CloudsTexture(), 0.8f, 0.8f)));
			// filterArray.add(new FilterInfo("", new TexturerFilter(
			// new LabyrinthTexture(), 0.8f, 0.8f)));
			// filterArray.add(new FilterInfo("", new TexturerFilter(
			// new MarbleTexture(), 1.8f, 0.8f)));
			// filterArray.add(new FilterInfo("", new TexturerFilter(
			// new WoodTexture(), 0.8f, 0.8f)));
			// filterArray.add(new FilterInfo("", new TexturerFilter(
			// new TextileTexture(), 0.8f, 0.8f)));
			// filterArray.add(new FilterInfo("", new HslModifyFilter(20f)));
			// filterArray.add(new FilterInfo("", new HslModifyFilter(40f)));
			// filterArray.add(new FilterInfo("", new HslModifyFilter(60f)));
			// filterArray.add(new FilterInfo("", new HslModifyFilter(80f)));
			// filterArray.add(new FilterInfo("", new HslModifyFilter(100f)));
			// filterArray.add(new FilterInfo("", new HslModifyFilter(150f)));
			// filterArray.add(new FilterInfo("", new HslModifyFilter(200f)));
			// filterArray.add(new FilterInfo("", new HslModifyFilter(250f)));
			// filterArray.add(new FilterInfo("", new HslModifyFilter(300f)));
			//
			// // v0.3
			// filterArray.add(new FilterInfo("", new ZoomBlurFilter(30)));
			// filterArray.add(new FilterInfo("", new ThreeDGridFilter(16,
			// 100)));
			// filterArray.add(new FilterInfo("", new ColorToneFilter(Color.rgb(
			// 33, 168, 254), 192)));
			// filterArray.add(new FilterInfo("", new ColorToneFilter(0x00FF00,
			// 192)));// green
			// filterArray.add(new FilterInfo("", new ColorToneFilter(0xFF0000,
			// 192)));// blue
			// filterArray.add(new FilterInfo("", new ColorToneFilter(0x00FFFF,
			// 192)));// yellow
			// filterArray.add(new FilterInfo("", new SoftGlowFilter(10, 0.1f,
			// 0.1f)));
			// filterArray
			// .add(new FilterInfo("", new TileReflectionFilter(20, 8)));
			// filterArray.add(new FilterInfo("", new BlindFilter(true, 96, 100,
			// 0xffffff)));
			// filterArray.add(new FilterInfo("", new BlindFilter(false, 96,
			// 100,
			// 0x000000)));
			// filterArray.add(new FilterInfo("", new RaiseFrameFilter(20)));
			// filterArray.add(new FilterInfo("", new ShiftFilter(10)));
			// filterArray.add(new FilterInfo("", new WaveFilter(25, 10)));
			// filterArray.add(new FilterInfo("", new BulgeFilter(-97)));
			// filterArray.add(new FilterInfo("", new TwistFilter(27, 106)));
			// filterArray.add(new FilterInfo("", new RippleFilter(38, 15,
			// true)));
			// filterArray.add(new FilterInfo("", new IllusionFilter(3)));
			// filterArray.add(new FilterInfo("", new SupernovaFilter(0x00FFFF,
			// 20, 100)));
			// filterArray.add(new FilterInfo("", new LensFlareFilter()));
			// filterArray.add(new FilterInfo("", new PosterizeFilter(2)));
			// filterArray.add(new FilterInfo("", new GammaFilter(50)));
			// filterArray.add(new FilterInfo("", new SharpFilter()));
			//
			// // v0.2
			// filterArray.add(new FilterInfo("", new ComicFilter()));
			// filterArray.add(new FilterInfo("", new SceneFilter(5f, Gradient
			// .Scene())));// green
			// filterArray.add(new FilterInfo("", new SceneFilter(5f, Gradient
			// .Scene1())));// purple
			// filterArray.add(new FilterInfo("", new SceneFilter(5f, Gradient
			// .Scene2())));// blue
			// filterArray.add(new FilterInfo("", new SceneFilter(5f, Gradient
			// .Scene3())));
			// filterArray.add(new FilterInfo("", new FilmFilter(80f)));
			// filterArray.add(new FilterInfo("", new FocusFilter()));
			// filterArray.add(new FilterInfo("", new CleanGlassFilter()));
			// filterArray
			// .add(new FilterInfo("", new PaintBorderFilter(0x00FF00)));//
			// green
			// filterArray
			// .add(new FilterInfo("", new PaintBorderFilter(0x00FFFF)));//
			// yellow
			// filterArray
			// .add(new FilterInfo("", new PaintBorderFilter(0xFF0000)));// blue
			// filterArray.add(new FilterInfo("", new LomoFilter()));
			//
			// // v0.1
			// filterArray.add(new FilterInfo("", new InvertFilter()));
			// filterArray.add(new FilterInfo("", new BlackWhiteFilter()));
			// filterArray.add(new FilterInfo("", new EdgeFilter()));
			// filterArray.add(new FilterInfo("", new PixelateFilter()));
			// filterArray.add(new FilterInfo("", new NeonFilter()));
			// filterArray.add(new FilterInfo("", new BigBrotherFilter()));
			// filterArray.add(new FilterInfo("", new MonitorFilter()));
			// filterArray.add(new FilterInfo("", new ReliefFilter()));
			// filterArray.add(new FilterInfo("", new BrightContrastFilter()));
			// filterArray.add(new FilterInfo("", new
			// SaturationModifyFilter()));
			// filterArray.add(new FilterInfo("", new ThresholdFilter()));
			// filterArray.add(new FilterInfo("", new NoiseFilter()));
			// filterArray.add(new FilterInfo("", new BannerFilter(10, true)));
			// filterArray.add(new FilterInfo("", new BannerFilter(10, false)));
			// filterArray.add(new FilterInfo("", new RectMatrixFilter()));
			// filterArray.add(new FilterInfo("", new BlockPrintFilter()));
			// filterArray.add(new FilterInfo("", new BrickFilter()));
			// filterArray.add(new FilterInfo("", new GaussianBlurFilter()));
			// filterArray.add(new FilterInfo("", new LightFilter()));
			// filterArray.add(new FilterInfo("", new MistFilter()));
			// filterArray.add(new FilterInfo("", new MosaicFilter()));
			// filterArray.add(new FilterInfo("", new OilPaintFilter()));
			// filterArray.add(new FilterInfo("", new
			// RadialDistortionFilter()));
			// filterArray.add(new FilterInfo("", new ReflectionFilter(true)));
			// filterArray.add(new FilterInfo("", new ReflectionFilter(false)));
			// filterArray.add(new FilterInfo("", new
			// SaturationModifyFilter()));
			// filterArray.add(new FilterInfo("", new SmashColorFilter()));
			// filterArray.add(new FilterInfo("", new TintFilter()));
			// filterArray.add(new FilterInfo("", new VignetteFilter()));
			// filterArray.add(new FilterInfo("", new AutoAdjustFilter()));
			// filterArray.add(new FilterInfo("", new ColorQuantizeFilter()));
			// filterArray.add(new FilterInfo("", new WaterWaveFilter()));
			// filterArray.add(new FilterInfo("", new VintageFilter()));
			// filterArray.add(new FilterInfo("", new OldPhotoFilter()));
			// filterArray.add(new FilterInfo("", new SepiaFilter()));
			// filterArray.add(new FilterInfo("", new RainBowFilter()));
			// filterArray.add(new FilterInfo("", new FeatherFilter()));
			// filterArray.add(new FilterInfo("", new XRadiationFilter()));
			// filterArray.add(new FilterInfo("", new NightVisionFilter()));

			// v0.4
			filterArray.add(new FilterInfo(R.drawable.video_filter1,
					new VideoFilter(VideoFilter.VIDEO_TYPE.VIDEO_STAGGERED)));
			filterArray.add(new FilterInfo(R.drawable.video_filter2,
					new VideoFilter(VideoFilter.VIDEO_TYPE.VIDEO_TRIPED)));
			filterArray.add(new FilterInfo(R.drawable.video_filter3,
					new VideoFilter(VideoFilter.VIDEO_TYPE.VIDEO_3X3)));
			filterArray.add(new FilterInfo(R.drawable.video_filter4,
					new VideoFilter(VideoFilter.VIDEO_TYPE.VIDEO_DOTS)));
			filterArray.add(new FilterInfo(R.drawable.tilereflection_filter1,
					new TileReflectionFilter(20, 8, 45, (byte) 1)));
			filterArray.add(new FilterInfo(R.drawable.tilereflection_filter2,
					new TileReflectionFilter(20, 8, 45, (byte) 2)));
			filterArray.add(new FilterInfo(R.drawable.fillpattern_filter,
					new FillPatternFilter(ImageFilterMain.this,
							R.drawable.texture1)));
			filterArray.add(new FilterInfo(R.drawable.fillpattern_filter1,
					new FillPatternFilter(ImageFilterMain.this,
							R.drawable.texture2)));
			filterArray.add(new FilterInfo(R.drawable.mirror_filter1,
					new MirrorFilter(true)));
			filterArray.add(new FilterInfo(R.drawable.mirror_filter2,
					new MirrorFilter(false)));
			filterArray.add(new FilterInfo(R.drawable.ycb_crlinear_filter,
					new YCBCrLinearFilter(new YCBCrLinearFilter.Range(-0.3f,
							0.3f))));
			filterArray
					.add(new FilterInfo(R.drawable.ycb_crlinear_filter2,
							new YCBCrLinearFilter(new YCBCrLinearFilter.Range(
									-0.276f, 0.163f),
									new YCBCrLinearFilter.Range(-0.202f, 0.5f))));
			filterArray.add(new FilterInfo(R.drawable.texturer_filter,
					new TexturerFilter(new CloudsTexture(), 0.8f, 0.8f)));
			filterArray.add(new FilterInfo(R.drawable.texturer_filter1,
					new TexturerFilter(new LabyrinthTexture(), 0.8f, 0.8f)));
			filterArray.add(new FilterInfo(R.drawable.texturer_filter2,
					new TexturerFilter(new MarbleTexture(), 1.8f, 0.8f)));
			filterArray.add(new FilterInfo(R.drawable.texturer_filter3,
					new TexturerFilter(new WoodTexture(), 0.8f, 0.8f)));
			filterArray.add(new FilterInfo(R.drawable.texturer_filter4,
					new TexturerFilter(new TextileTexture(), 0.8f, 0.8f)));
			filterArray.add(new FilterInfo(R.drawable.hslmodify_filter,
					new HslModifyFilter(20f)));
			filterArray.add(new FilterInfo(R.drawable.hslmodify_filter0,
					new HslModifyFilter(40f)));
			filterArray.add(new FilterInfo(R.drawable.hslmodify_filter1,
					new HslModifyFilter(60f)));
			filterArray.add(new FilterInfo(R.drawable.hslmodify_filter2,
					new HslModifyFilter(80f)));
			filterArray.add(new FilterInfo(R.drawable.hslmodify_filter3,
					new HslModifyFilter(100f)));
			filterArray.add(new FilterInfo(R.drawable.hslmodify_filter4,
					new HslModifyFilter(150f)));
			filterArray.add(new FilterInfo(R.drawable.hslmodify_filter5,
					new HslModifyFilter(200f)));
			filterArray.add(new FilterInfo(R.drawable.hslmodify_filter6,
					new HslModifyFilter(250f)));
			filterArray.add(new FilterInfo(R.drawable.hslmodify_filter7,
					new HslModifyFilter(300f)));

			// v0.3
			filterArray.add(new FilterInfo(R.drawable.zoomblur_filter,
					new ZoomBlurFilter(30)));
			filterArray.add(new FilterInfo(R.drawable.threedgrid_filter,
					new ThreeDGridFilter(16, 100)));
			filterArray.add(new FilterInfo(R.drawable.colortone_filter,
					new ColorToneFilter(Color.rgb(33, 168, 254), 192)));
			filterArray.add(new FilterInfo(R.drawable.colortone_filter2,
					new ColorToneFilter(0x00FF00, 192)));// green
			filterArray.add(new FilterInfo(R.drawable.colortone_filter3,
					new ColorToneFilter(0xFF0000, 192)));// blue
			filterArray.add(new FilterInfo(R.drawable.colortone_filter4,
					new ColorToneFilter(0x00FFFF, 192)));// yellow
			filterArray.add(new FilterInfo(R.drawable.softglow_filter,
					new SoftGlowFilter(10, 0.1f, 0.1f)));
			filterArray.add(new FilterInfo(R.drawable.tilereflection_filter,
					new TileReflectionFilter(20, 8)));
			filterArray.add(new FilterInfo(R.drawable.blind_filter1,
					new BlindFilter(true, 96, 100, 0xffffff)));
			filterArray.add(new FilterInfo(R.drawable.blind_filter2,
					new BlindFilter(false, 96, 100, 0x000000)));
			filterArray.add(new FilterInfo(R.drawable.raiseframe_filter,
					new RaiseFrameFilter(20)));
			filterArray.add(new FilterInfo(R.drawable.shift_filter,
					new ShiftFilter(10)));
			filterArray.add(new FilterInfo(R.drawable.wave_filter,
					new WaveFilter(25, 10)));
			filterArray.add(new FilterInfo(R.drawable.bulge_filter,
					new BulgeFilter(-97)));
			filterArray.add(new FilterInfo(R.drawable.twist_filter,
					new TwistFilter(27, 106)));
			filterArray.add(new FilterInfo(R.drawable.ripple_filter,
					new RippleFilter(38, 15, true)));
			filterArray.add(new FilterInfo(R.drawable.illusion_filter,
					new IllusionFilter(3)));
			filterArray.add(new FilterInfo(R.drawable.supernova_filter,
					new SupernovaFilter(0x00FFFF, 20, 100)));
			filterArray.add(new FilterInfo(R.drawable.lensflare_filter,
					new LensFlareFilter()));
			filterArray.add(new FilterInfo(R.drawable.posterize_filter,
					new PosterizeFilter(2)));
			filterArray.add(new FilterInfo(R.drawable.gamma_filter,
					new GammaFilter(50)));
			filterArray.add(new FilterInfo(R.drawable.sharp_filter,
					new SharpFilter()));

			// v0.2
			filterArray.add(new FilterInfo(R.drawable.invert_filter,
					new ComicFilter()));
			filterArray.add(new FilterInfo(R.drawable.invert_filter,
					new SceneFilter(5f, Gradient.Scene())));// green
			filterArray.add(new FilterInfo(R.drawable.invert_filter,
					new SceneFilter(5f, Gradient.Scene1())));// purple
			filterArray.add(new FilterInfo(R.drawable.invert_filter,
					new SceneFilter(5f, Gradient.Scene2())));// blue
			filterArray.add(new FilterInfo(R.drawable.invert_filter,
					new SceneFilter(5f, Gradient.Scene3())));
			filterArray.add(new FilterInfo(R.drawable.invert_filter,
					new FilmFilter(80f)));
			filterArray.add(new FilterInfo(R.drawable.invert_filter,
					new FocusFilter()));
			filterArray.add(new FilterInfo(R.drawable.invert_filter,
					new CleanGlassFilter()));
			filterArray.add(new FilterInfo(R.drawable.invert_filter,
					new PaintBorderFilter(0x00FF00)));// green
			filterArray.add(new FilterInfo(R.drawable.invert_filter,
					new PaintBorderFilter(0x00FFFF)));// yellow
			filterArray.add(new FilterInfo(R.drawable.invert_filter,
					new PaintBorderFilter(0xFF0000)));// blue
			filterArray.add(new FilterInfo(R.drawable.invert_filter,
					new LomoFilter()));

			// v0.1
			filterArray.add(new FilterInfo(R.drawable.invert_filter,
					new InvertFilter()));
			filterArray.add(new FilterInfo(R.drawable.blackwhite_filter,
					new BlackWhiteFilter()));
			filterArray.add(new FilterInfo(R.drawable.edge_filter,
					new EdgeFilter()));
			filterArray.add(new FilterInfo(R.drawable.pixelate_filter,
					new PixelateFilter()));
			filterArray.add(new FilterInfo(R.drawable.neon_filter,
					new NeonFilter()));
			filterArray.add(new FilterInfo(R.drawable.bigbrother_filter,
					new BigBrotherFilter()));
			filterArray.add(new FilterInfo(R.drawable.monitor_filter,
					new MonitorFilter()));
			filterArray.add(new FilterInfo(R.drawable.relief_filter,
					new ReliefFilter()));
			filterArray.add(new FilterInfo(R.drawable.brightcontrast_filter,
					new BrightContrastFilter()));
			filterArray.add(new FilterInfo(R.drawable.saturationmodity_filter,
					new SaturationModifyFilter()));
			filterArray.add(new FilterInfo(R.drawable.threshold_filter,
					new ThresholdFilter()));
			filterArray.add(new FilterInfo(R.drawable.noisefilter,
					new NoiseFilter()));
			filterArray.add(new FilterInfo(R.drawable.banner_filter1,
					new BannerFilter(10, true)));
			filterArray.add(new FilterInfo(R.drawable.banner_filter2,
					new BannerFilter(10, false)));
			filterArray.add(new FilterInfo(R.drawable.rectmatrix_filter,
					new RectMatrixFilter()));
			filterArray.add(new FilterInfo(R.drawable.blockprint_filter,
					new BlockPrintFilter()));
			filterArray.add(new FilterInfo(R.drawable.brick_filter,
					new BrickFilter()));
			filterArray.add(new FilterInfo(R.drawable.gaussianblur_filter,
					new GaussianBlurFilter()));
			filterArray.add(new FilterInfo(R.drawable.light_filter,
					new LightFilter()));
			filterArray.add(new FilterInfo(R.drawable.mosaic_filter,
					new MistFilter()));
			filterArray.add(new FilterInfo(R.drawable.mosaic_filter,
					new MosaicFilter()));
			filterArray.add(new FilterInfo(R.drawable.oilpaint_filter,
					new OilPaintFilter()));
			filterArray.add(new FilterInfo(R.drawable.radialdistortion_filter,
					new RadialDistortionFilter()));
			filterArray.add(new FilterInfo(R.drawable.reflection1_filter,
					new ReflectionFilter(true)));
			filterArray.add(new FilterInfo(R.drawable.reflection2_filter,
					new ReflectionFilter(false)));
			filterArray.add(new FilterInfo(R.drawable.saturationmodify_filter,
					new SaturationModifyFilter()));
			filterArray.add(new FilterInfo(R.drawable.smashcolor_filter,
					new SmashColorFilter()));
			filterArray.add(new FilterInfo(R.drawable.tint_filter,
					new TintFilter()));
			filterArray.add(new FilterInfo(R.drawable.vignette_filter,
					new VignetteFilter()));
			filterArray.add(new FilterInfo(R.drawable.autoadjust_filter,
					new AutoAdjustFilter()));
			filterArray.add(new FilterInfo(R.drawable.colorquantize_filter,
					new ColorQuantizeFilter()));
			filterArray.add(new FilterInfo(R.drawable.waterwave_filter,
					new WaterWaveFilter()));
			filterArray.add(new FilterInfo(R.drawable.vintage_filter,
					new VintageFilter()));
			filterArray.add(new FilterInfo(R.drawable.oldphoto_filter,
					new OldPhotoFilter()));
			filterArray.add(new FilterInfo(R.drawable.sepia_filter,
					new SepiaFilter()));
			filterArray.add(new FilterInfo(R.drawable.rainbow_filter,
					new RainBowFilter()));
			filterArray.add(new FilterInfo(R.drawable.feather_filter,
					new FeatherFilter()));
			filterArray.add(new FilterInfo(R.drawable.xradiation_filter,
					new XRadiationFilter()));
			filterArray.add(new FilterInfo(R.drawable.nightvision_filter,
					new NightVisionFilter()));

			filterArray.add(new FilterInfo(R.drawable.saturationmodity_filter,
					new IImageFilter() {

						@Override
						public Image process(Image imageIn) {
							return imageIn;
						}
					}/* /* �˴������ԭͼЧ�� */));
		}

		public int getCount() {
			return filterArray.size();
		}

		public Object getItem(int position) {
			return position < filterArray.size() ? filterArray.get(position).filter
					: null;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ImageView imageView = null;
			if (convertView == null) {
				int width = 100;// bmImg.getWidth();
				int height = 100;// bmImg.getHeight();
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new Gallery.LayoutParams(width,
						height));
				imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);// ������ʾ��������
				convertView = imageView;
			} else {
				imageView = (ImageView) convertView;
			}
			IImageFilter imgFilter = filterArray.get(position).filter;
			imageView.setTag(imgFilter);
			Bitmap bitmap = BitmapManagerUtils
					.get(filterArray.get(position).filterID + "");
			if (bitmap == null) {
				System.out.println("loadImage");
				loadImage(filterArray.get(position).filterID, imgFilter,
						new OnLoadCompleteListenerImp(imageView));
			} else {
				System.out.println("setImageBitmap");
				imageView.setImageBitmap(bitmap);
			}
			return convertView;
		}
	};

	private int currFilterPosition = -1;
	private boolean isFilting = false;

	private ExecutorService mExecutorService;

	public void initThread() {
		int threads = Runtime.getRuntime().availableProcessors();
		mExecutorService = Executors.newFixedThreadPool(threads);
	}

	public void loadImage(final int resId, final IImageFilter filter,
			final OnLoadCompleteListener callback) {

		mExecutorService.submit(new Runnable() {

			@Override
			public void run() {
				Bitmap bitmap = BitmapUtils.scaleBitmapRes(getBaseContext(),
						R.drawable.effect_src, 100);
				BitmapManagerUtils.put("" + resId, bitmap);
				final Image img = filter.process(new Image(bitmap));
				img.copyPixelsFromBuffer();
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						if (callback != null) {
							callback.onComplete(filter, img);
						}
					}
				});
			}
		});
	}

	private class OnLoadCompleteListenerImp implements OnLoadCompleteListener {

		private ImageView imageView = null;

		public OnLoadCompleteListenerImp(ImageView imageView) {
			this.imageView = imageView;
		}

		@Override
		public void onComplete(IImageFilter filter, Image img) {
			if (imageView.getTag() == filter) {
				Bitmap bitmap = img.getImage();
				imageView.setImageBitmap(bitmap);
			} else {
				img.getImage().recycle();
			}
		}
	}

	public interface OnLoadCompleteListener {
		void onComplete(IImageFilter filter, Image img);
	}

	public static String REQUEST_TYPE = "REQUEST_TYPE";
	public static final int REQUEST_CODE_PICK_IMAGE = 999;

	protected void getImageFromAlbum() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
	}

	public static final int REQUEST_CODE_CAPTURE_CAMEIA = 888;

	private String mCurrFileName;
	
	
	protected void getImageFromCamera() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			Intent intent = new Intent();
			intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			mCurrFileName = Environment.getExternalStorageDirectory()
					+ File.separator + "pic" + File.separator + "IMG_"
					+ System.currentTimeMillis() + ".jpg";
			// 根据文件地址创建文件
			File file = new File(mCurrFileName);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			// 把文件地址转换成Uri格式
			Uri uri = Uri.fromFile(file);
			// 设置系统相机拍摄照片完成后图片文件的存放地址
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMEIA);
		} else {
			Toast.makeText(getApplicationContext(), "无法检测到SD卡!",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_PICK_IMAGE) {
			if (resultCode != RESULT_OK || data == null) {
				finish();
				return;
			}
			Uri uri = data.getData();

			try {
				Bitmap bitmap = MediaStore.Images.Media.getBitmap(
						this.getContentResolver(), uri);
				if (bitmap != null) {
					showBitmap(bitmap);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				finish();
			} catch (IOException e) {
				e.printStackTrace();
				finish();
			}
		} else if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
			if (resultCode != RESULT_OK || TextUtils.isEmpty(mCurrFileName)) {
				finish();
				return;
			}
			new Handler(getMainLooper()).postDelayed(new Runnable() {
				
				@Override
				public void run() {
					showBitmapFromFile();
				}
			}, 100);
		}
	}

	private Bitmap mCurrShowBitmap;
	private Bitmap mCurrSrcBitmap;

	private final String CURR_BITMAP = "CURR_BITMAP";

	private void showBitmap(Bitmap bitmap) {
		System.out.println(" bitmap :" + bitmap.getWidth() + " , h : "
				+ bitmap.getHeight() + " ,count :" + bitmap.getRowBytes()
				* bitmap.getHeight());
		float scale = BitmapManagerUtils.getScale(bitmap);
		mCurrSrcBitmap = BitmapUtils.scaleBitmap(bitmap, scale);
		System.out.println("scale  :" + scale);
		System.out.println(" mCurrSrcBitmap :" + mCurrSrcBitmap.getWidth()
				+ " , h : " + mCurrSrcBitmap.getHeight() + " ,mCurrSrcBitmap :"
				+ mCurrSrcBitmap.getRowBytes() * mCurrSrcBitmap.getHeight());
		BitmapManagerUtils.put(CURR_BITMAP, mCurrSrcBitmap);
		imageView.setImageBitmap(mCurrSrcBitmap);
	}
	
	private void showBitmapFromFile() {
		mCurrSrcBitmap = BitmapUtils.scaleSingleBitmapFile(mCurrFileName, BitmapManagerUtils.getSingleBitmapMaxSpace());
		System.out.println(" mCurrSrcBitmap :" + mCurrSrcBitmap.getWidth()
				+ " , h : " + mCurrSrcBitmap.getHeight() + " ,mCurrSrcBitmap :"
				+ mCurrSrcBitmap.getRowBytes() * mCurrSrcBitmap.getHeight());
		BitmapManagerUtils.put(CURR_BITMAP, mCurrSrcBitmap);

		imageView.setImageBitmap(mCurrSrcBitmap);
	}

	private void showImageBitmap(Bitmap bitmap) {
		mCurrShowBitmap = bitmap;
		imageView.setImageBitmap(mCurrShowBitmap);
	}

}
