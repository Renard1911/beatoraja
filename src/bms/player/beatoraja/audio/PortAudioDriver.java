package bms.player.beatoraja.audio;

import java.io.IOException;
import java.nio.file.*;
import java.util.logging.Logger;

import com.portaudio.*;

import bms.player.beatoraja.Config;

public class PortAudioDriver extends AbstractAudioDriver<PCM> {

	private static DeviceInfo[] devices;
	
	private BlockingStream stream;
	private float[] buffer;
	private int sampleRate;
	/**
	 * オーディオミキサー
	 */
	private AudioMixer mixer;
	
	public static DeviceInfo[] getDevices() {
		if(devices == null) {
			PortAudio.initialize();
			
			devices = new DeviceInfo[PortAudio.getDeviceCount()];
			for(int i = 0;i < devices.length;i++) {
				devices[i] = PortAudio.getDeviceInfo(i);
			}
		}
		return devices;
	}

	public PortAudioDriver(Config config) {

		DeviceInfo[] devices = getDevices();
		// Get the default device and setup the stream parameters.
		int deviceId = PortAudio.getDefaultOutputDevice();
		DeviceInfo deviceInfo = devices[ deviceId ];
		sampleRate = (int)deviceInfo.defaultSampleRate;
		System.out.println( "  deviceId    = " + deviceId );
		System.out.println( "  sampleRate  = " + sampleRate );
		System.out.println( "  device name = " + deviceInfo.name );

		StreamParameters streamParameters = new StreamParameters();
		streamParameters.channelCount = 2;
		streamParameters.device = deviceId;
		streamParameters.suggestedLatency = deviceInfo.defaultLowOutputLatency;
		System.out.println( "  suggestedLatency = "
				+ streamParameters.suggestedLatency );

		int framesPerBuffer = 512;
		int flags = 0;
		
		// Open a stream for output.
		stream = PortAudio.openStream( null, streamParameters,
				(int) sampleRate, framesPerBuffer, flags );
		buffer = new float[framesPerBuffer * 2];

		stream.start();

		mixer = new AudioMixer(config.getAudioDeviceSimultaneousSources());
		mixer.start();
	}

	@Override
	protected PCM getKeySound(Path p) {
		String name = p.toString();
		name = name.substring(0, name.lastIndexOf('.'));
		final Path wavfile = Paths.get(name + ".wav");
		final Path oggfile = Paths.get(name + ".ogg");
		final Path mp3file = Paths.get(name + ".mp3");

		PCM wav = null;
		if (wav == null && Files.exists(wavfile)) {
			try {
				wav = new PCM(wavfile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (wav == null && Files.exists(oggfile)) {
			try {
				wav = new PCM(oggfile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (wav == null && Files.exists(mp3file)) {
			try {
				wav = new PCM(mp3file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (wav != null && wav.getSampleRate() != sampleRate) {
			wav = wav.changeSampleRate(sampleRate);
		}
		if (wav != null && wav.getChannels() != 2) {
			wav = wav.changeChannels(2);
		}

		return wav;
	}

	@Override
	protected PCM getKeySound(PCM pcm) {
		if (pcm.getSampleRate() != sampleRate) {
			pcm = pcm.changeSampleRate(sampleRate);
		}
		if (pcm.getChannels() != 2) {
			pcm = pcm.changeChannels(2);
		}
		return pcm;
	}

	@Override
	protected synchronized void play(PCM id, float volume) {
		mixer.put(id, volume, false);
	}

	@Override
	protected void play(AudioElement<PCM> id, float volume, boolean loop) {
		id.id = mixer.put(id.audio, volume, loop);
	}

	@Override
	protected void setVolume(AudioElement<PCM> id, float volume) {
		mixer.setVolume(id.id, volume);
	}

	@Override
	protected void stop(PCM id) {
		mixer.stop(id);
	}

	@Override
	protected void disposeKeySound(PCM pcm) {
	}

	public void dispose() {
		super.dispose();
		if(stream != null) {
			mixer.stop = true;
			long l = System.currentTimeMillis();
			while(mixer.isAlive() && System.currentTimeMillis() - l < 1000);
			stream.stop();
			stream.close();
			
			stream = null;

			PortAudio.terminate();
			System.out.println( "JPortAudio test complete." );			
		}
	}
	
	/**
	 * オーディオミキサー
	 *
	 * @author exch
	 */
	class AudioMixer extends Thread {

		/**
		 * ミキサー入力
		 */
		private MixerInput[] inputs;

		private long idcount;
		
		private boolean stop = false;

		public AudioMixer(int channels) {
			inputs = new MixerInput[channels];
			for (int i = 0; i < inputs.length; i++) {
				inputs[i] = new MixerInput();
			}
		}

		public long put(PCM pcm, float volume, boolean loop) {
			for (int i = 0; i < inputs.length; i++) {
				if (inputs[i].pos == -1) {
					inputs[i].pcm = pcm;
					inputs[i].sample = pcm.getSample();
					inputs[i].volume = volume;
					inputs[i].pos = 0;
					inputs[i].loop = loop;
					inputs[i].id = idcount++;
					return inputs[i].id;
				}
			}
			return -1;
		}

		public void setVolume(long id, float volume) {
			for (int i = 0; i < inputs.length; i++) {
				if (inputs[i].id == id) {
					inputs[i].volume = volume;
					break;
				}
			}
		}

		public void stop(PCM id) {
			for (int i = 0; i < inputs.length; i++) {
				if (inputs[i].pcm == id) {
					inputs[i].pos = -1;
				}
			}
		}

		public void run() {
			while(!stop) {
				for (int i = 0; i < buffer.length; i++) {
					float wav = 0;
					for (MixerInput input : inputs) {
						if (input.pos != -1) {
							wav += ((float) input.sample[input.pos]) * input.volume / Short.MAX_VALUE;
							input.pos++;
							if (input.pos == input.sample.length) {
								input.pos = input.loop ? 0 : -1;
							}
						}
					}
					buffer[i] = wav;
				}
				stream.write( buffer, buffer.length / 2);
			}
		}		
	}

	class MixerInput {
		public PCM pcm;
		public short[] sample = new short[0];
		public float volume;
		public int pos = -1;
		public boolean loop;
		public long id;
	}
}
