package bms.player.beatoraja;

import java.util.Map;
import bms.player.beatoraja.play.JudgeAlgorithm;
import bms.player.beatoraja.skin.SkinType;
import static bms.player.beatoraja.Resolution.*;

/**
 * 各種設定項目。config.jsonで保持される
 * 
 * @author exch
 */
public class Config {

	// TODO プレイヤー毎に異なる見込みの大きい要素をPlayerConfigに移動

	private String playername;
	/**
	 * フルスクリーン
	 */
	private boolean fullscreen;
	/**
	 * 垂直同期
	 */
	private boolean vsync;
	/**
	 * 解像度
	 */
	private Resolution resolution = HD;

	/**
	 * フォルダランプの有効/無効
	 */
	private boolean folderlamp = true;
	/**
	 * オーディオドライバー
	 */
	private int audioDriver = 0;
	/**
	 * オーディオ:OpenAL (libGDX Sound)
	 */
	public static final int AUDIODRIVER_SOUND = 0;
	public static final int AUDIODRIVER_AUDIODEVICE = 1;
	
	/**
	 * オーディオ:PortAudio
	 */
	public static final int AUDIODRIVER_PORTAUDIO = 2;
	/**
	 * オーディオ:JASIOHost
	 */
	public static final int AUDIODRIVER_ASIO = 3;

	private String audioDriverName = null;
	/**
	 * オーディオバッファサイズ。大きすぎると音声遅延が発生し、少なすぎるとノイズが発生する
	 */
	private int audioDeviceBufferSize = 384;
	/**
	 * オーディオ同時発音数
	 */
	private int audioDeviceSimultaneousSources = 64;

	/**
	 * オーディオ再生速度変化の処理:なし
	 */
	public static final int AUDIO_PLAY_UNPROCESSED = 0;
	/**
	 * オーディオ再生速度変化の処理:周波数を合わせる(速度に応じてピッチも変化)
	 */
	public static final int AUDIO_PLAY_FREQ = 1;
	/**
	 * オーディオ再生速度変化の処理:ピッチ変化なしに速度を変更(未実装)
	 */
	public static final int AUDIO_PLAY_SPEED = 1;
	/**
	 * PracticeモードのFREQUENCYオプションに対する音声処理方法
	 */
	private int audioFreqOption = AUDIO_PLAY_FREQ;
	/**
	 * 早送り再生に対する音声処理方法
	 */
	private int audioFastForward = AUDIO_PLAY_FREQ;

	/**
	 * システム音ボリューム
	 */
	private float systemvolume = 1.0f;
	/**
	 * キー音のボリューム
	 */
	private float keyvolume = 1.0f;
	/**
	 * BGノート音のボリューム
	 */
	private float bgvolume = 1.0f;
	/**
	 * 最大FPS。垂直同期OFFの時のみ有効
	 */
	private int maxFramePerSecond = 240;
	/**
	 * 最小入力感覚
	 */
	private int inputduration = 10;
	/**
	 * 選曲バー移動速度の最初
	 */
	private int scrolldurationlow = 300;
	/**
	 * 選曲バー移動速度の2つ目以降
	 */
	private int scrolldurationhigh = 50;
	/**
	 * 判定アルゴリズム
	 */
	private JudgeAlgorithm judgealgorithm = JudgeAlgorithm.Combo;
	
    private boolean cacheSkinImage = false;
    
    private boolean useSongInfo = true;
    
	private boolean showhiddennote = false;

	private boolean showpastnote = false;

	private String bgmpath = "";

	private String soundpath = "";

	private SkinConfig[] skin;
	/**
	 * BMSルートディレクトリパス
	 */
	private String[] bmsroot = new String[0];
	/**
	 * 難易度表URL
	 */
	private String[] tableURL = new String[0];
	/**
	 * BGA表示
	 */
	private int bga = BGA_ON;
	public static final int BGA_ON = 0;
	public static final int BGA_AUTO = 1;
	public static final int BGA_OFF = 2;
	/**
	 * BGA拡大
	 */
	private int bgaExpand = BGAEXPAND_FULL;
	public static final int BGAEXPAND_FULL = 0;
	public static final int BGAEXPAND_KEEP_ASPECT_RATIO = 1;
	public static final int BGAEXPAND_OFF = 2;

	private int movieplayer = MOVIEPLAYER_FFMPEG;
	public static final int MOVIEPLAYER_FFMPEG = 0;
	public static final int MOVIEPLAYER_VLC = 1;

	private int frameskip = 1;
	private String vlcpath = "";

	private boolean updatesong = false;

	private int autosavereplay[] = {0,0,0,0};

	public Config() {
		tableURL = new String[] { "http://bmsnormal2.syuriken.jp/table.html",
				"http://bmsnormal2.syuriken.jp/table_insane.html", "http://dpbmsdelta.web.fc2.com/table/dpdelta.html",
				"http://dpbmsdelta.web.fc2.com/table/insane.html",
				"http://flowermaster.web.fc2.com/lrnanido/gla/LN.html",
				"http://stellawingroad.web.fc2.com/new/pms.html" };
		int maxSkinType = 0;
		for (SkinType type : SkinType.values()) {
			if (type.getId() > maxSkinType) {
				maxSkinType = type.getId();
			}
		}
		skin = new SkinConfig[maxSkinType + 1];
		for (Map.Entry<SkinType, String> entry : SkinConfig.defaultSkinPathMap.entrySet()) {
			skin[entry.getKey().getId()] = new SkinConfig(entry.getValue());
		}
	}

	public String getPlayername() {
		return playername;
	}

	public void setPlayername(String playername) {
		this.playername = playername;
	}

	public boolean isFullscreen() {
		return fullscreen;
	}

	public void setFullscreen(boolean fullscreen) {
		this.fullscreen = fullscreen;
	}

	public boolean isVsync() {
		return vsync;
	}

	public void setVsync(boolean vsync) {
		this.vsync = vsync;
	}

	public int getBga() {
		return bga;
	}

	public void setBga(int bga) {
		this.bga = bga;
	}

	public String getVlcpath() {
		return vlcpath;
	}

	public void setVlcpath(String vlcpath) {
		this.vlcpath = vlcpath;
	}

	public int getAudioDeviceBufferSize() {
		return audioDeviceBufferSize;
	}

	public void setAudioDeviceBufferSize(int audioDeviceBufferSize) {
		this.audioDeviceBufferSize = audioDeviceBufferSize;
	}

	public int getAudioDeviceSimultaneousSources() {
		return audioDeviceSimultaneousSources;
	}

	public void setAudioDeviceSimultaneousSources(int audioDeviceSimultaneousSources) {
		this.audioDeviceSimultaneousSources = audioDeviceSimultaneousSources;
	}

	public int getMaxFramePerSecond() {
		return maxFramePerSecond;
	}

	public void setMaxFramePerSecond(int maxFramePerSecond) {
		this.maxFramePerSecond = maxFramePerSecond;
	}

	public String[] getBmsroot() {
		return bmsroot;
	}

	public void setBmsroot(String[] bmsroot) {
		this.bmsroot = bmsroot;
	}

	public String[] getTableURL() {
		return tableURL;
	}

	public void setTableURL(String[] tableURL) {
		this.tableURL = tableURL;
	}

	public JudgeAlgorithm getJudgealgorithm() {
		return judgealgorithm;
	}

	public void setJudgealgorithm(JudgeAlgorithm judgeAlgorithm) {
		this.judgealgorithm = judgeAlgorithm;
	}

	public boolean isFolderlamp() {
		return folderlamp;
	}

	public void setFolderlamp(boolean folderlamp) {
		this.folderlamp = folderlamp;
	}

	public Resolution getResolution() {
		return resolution;
	}

	public void setResolution(Resolution resolution) {
		this.resolution = resolution;
	}

	public boolean isShowhiddennote() {
		return showhiddennote;
	}

	public void setShowhiddennote(boolean showhiddennote) {
		this.showhiddennote = showhiddennote;
	}

	public int getMovieplayer() {
		return movieplayer;
	}

	public void setMovieplayer(int movieplayer) {
		this.movieplayer = movieplayer;
	}

	public int getFrameskip() {
		return frameskip;
	}

	public void setFrameskip(int frameskip) {
		this.frameskip = frameskip;
	}

	public String getBgmpath() {
		return bgmpath;
	}

	public void setBgmpath(String bgmpath) {
		this.bgmpath = bgmpath;
	}

	public String getSoundpath() {
		return soundpath;
	}

	public void setSoundpath(String soundpath) {
		this.soundpath = soundpath;
	}

	public int getInputduration() {
		return inputduration;
	}

	public void setInputduration(int inputduration) {
		this.inputduration = inputduration;
	}

	public int getScrollDurationLow(){
		return scrolldurationlow;
	}
	public void setScrollDutationLow(int scrolldurationlow){
		this.scrolldurationlow = scrolldurationlow;
	}
	public int getScrollDurationHigh(){
		return scrolldurationhigh;
	}
	public void setScrollDutationHigh(int scrolldurationhigh){
		this.scrolldurationhigh = scrolldurationhigh;
	}

	public float getKeyvolume() {
		if(keyvolume < 0 || keyvolume > 1) {
			keyvolume = 1;
		}
		return keyvolume;
	}

	public void setKeyvolume(float keyvolume) {
		this.keyvolume = keyvolume;
	}

	public float getBgvolume() {
		if(bgvolume < 0 || bgvolume > 1) {
			bgvolume = 1;
		}
		return bgvolume;
	}

	public void setBgvolume(float bgvolume) {
		this.bgvolume = bgvolume;
	}

	public boolean isShowpastnote() {
		return showpastnote;
	}

	public void setShowpastnote(boolean showpastnote) {
		this.showpastnote = showpastnote;
	}

	public int getAudioDriver() {
		return audioDriver;
	}

	public void setAudioDriver(int audioDriver) {
		this.audioDriver = audioDriver;
	}

	public String getAudioDriverName() {
		return audioDriverName;
	}

	public void setAudioDriverName(String audioDriverName) {
		this.audioDriverName = audioDriverName;
	}

	public int getAudioFreqOption() {
		return audioFreqOption;
	}

	public void setAudioFreqOption(int audioFreqOption) {
		this.audioFreqOption = audioFreqOption;
	}

	public int getAudioFastForward() {
		return audioFastForward;
	}

	public void setAudioFastForward(int audioFastForward) {
		this.audioFastForward = audioFastForward;
	}

	public void setAutoSaveReplay(int autoSaveReplay[]){
		this.autosavereplay = autoSaveReplay;
	}

	public int[] getAutoSaveReplay(){
		return autosavereplay;
	}
	
	public boolean isUseSongInfo() {
		return useSongInfo;
	}

	public void setUseSongInfo(boolean useSongInfo) {
		this.useSongInfo = useSongInfo;
	}

	public int getBgaExpand() {
		return bgaExpand;
	}

	public void setBgaExpand(int bgaExpand) {
		this.bgaExpand = bgaExpand;
	}

	public boolean isCacheSkinImage() {
		return cacheSkinImage;
	}

	public void setCacheSkinImage(boolean cacheSkinImage) {
		this.cacheSkinImage = cacheSkinImage;
	}

	public float getSystemvolume() {
		if(systemvolume < 0 || systemvolume > 1) {
			systemvolume = 1;
		}
		return systemvolume;
	}

	public void setSystemvolume(float systemvolume) {
		this.systemvolume = systemvolume;
	}
	
	public boolean isUpdatesong() {
		return updatesong;
	}

	public void setUpdatesong(boolean updatesong) {
		this.updatesong = updatesong;
	}

	// TODO これ以降の値はPlayerConfigに移行する	
}
