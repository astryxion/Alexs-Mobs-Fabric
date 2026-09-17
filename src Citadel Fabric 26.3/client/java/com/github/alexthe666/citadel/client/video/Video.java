package com.github.alexthe666.citadel.client.video;

import com.github.alexthe666.citadel.client.texture.VideoFrameTexture;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Video {

    public static final Logger LOGGER = LogManager.getLogger("citadel-video");

    private boolean paused;
    private boolean hasAudioLoaded;
    private boolean repeat;

    private boolean muted;

    private String url;
    private Identifier Identifier;
    private VideoFrameTexture texture;

    private Object frameGrabber = null;
    private Object prevFrameGrabber = null;
    private File mp4FileOnDisk = null;
    private double framesPerSecond;
    private long startTime = -1;
    private int lastFrame = -1;
    private long pausedAudioTime = 0;
    private Clip audioClip;

    public Video(String url, Identifier Identifier, VideoFrameTexture texture, double framesPerSecond, boolean muted) {
        this.url = url;
        this.Identifier = Identifier;
        this.texture = texture;
        this.framesPerSecond = framesPerSecond;
        this.muted = muted;
        setupFrameGrabber();
    }

    public void update() {

        if (frameGrabber != null) {
            if(prevFrameGrabber == null){
                onStart();
            }
            long milliseconds = System.currentTimeMillis() - startTime;
            int frame = (int) (milliseconds / 1000D * framesPerSecond);
            pausedAudioTime = milliseconds * 1000;
            if(lastFrame == frame || this.paused){
                return;
            }else{
                lastFrame = frame;
            }
            try {
                Object picture = invoke(frameGrabber, "getNativeFrame");
                if (picture != null) {
                    BufferedImage bufferedImage = toBufferedImage(picture);
                    texture.setPixelsFromBufferedImage(bufferedImage);
                } else if(repeat){
                    invoke(frameGrabber, "seekToFramePrecise", new Class[]{int.class}, new Object[]{0});
                    if(audioClip != null && !this.muted){
                        audioClip.loop(-1);
                        audioClip.setFramePosition(0);
                    }
                    startTime = System.currentTimeMillis();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        prevFrameGrabber = frameGrabber;
    }

    public void onStart(){
        startTime = System.currentTimeMillis();
    }

    private void setupFrameGrabber() {
        final ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.submit(() -> {
            try {
                InputStream in = new URL(url).openStream();
                Path path = Paths.get(getVideoCacheFolder().toString(), Identifier.getPath());
                Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
                in.close();
                mp4FileOnDisk = path.toFile();
                Class<?> nioUtilsClass = Class.forName("org.jcodec.common.io.NIOUtils");
                Object channel = nioUtilsClass.getMethod("readableChannel", File.class).invoke(null, mp4FileOnDisk);
                Class<?> frameGrabClass = Class.forName("org.jcodec.api.FrameGrab");
                Class<?> readableByteChannel = Class.forName("java.nio.channels.ReadableByteChannel");
                frameGrabber = frameGrabClass.getMethod("createFrameGrab", readableByteChannel).invoke(null, channel);
                LOGGER.info("loaded mp4 video from {}", url);
                if(!this.muted){
                    setupAudio(mp4FileOnDisk, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private void setupAudio(File mp4File, long time) {
        try {
            Class<?> aacReaderClass = Class.forName("net.sourceforge.jaad.spi.javasound.AACAudioFileReader");
            Object aacAudioFileReader = aacReaderClass.getDeclaredConstructor().newInstance();
            AudioInputStream audioInputStream = (AudioInputStream) aacReaderClass.getMethod("getAudioInputStream", File.class).invoke(aacAudioFileReader, mp4File);
            audioClip = AudioSystem.getClip();

            audioClip.open(audioInputStream);

            audioClip.setMicrosecondPosition(time);
            audioClip.start();
            if(!hasAudioLoaded){
                LOGGER.info("loaded mp4 audio from {}", url);
            }
            hasAudioLoaded = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
        if(audioClip != null && hasAudioLoaded){
            if(paused || this.muted){
                if(audioClip.isOpen()){
                    audioClip.close();
                }
            }else{
                if(!audioClip.isOpen()){
                    setupAudio(mp4FileOnDisk, pausedAudioTime);
                }
            }
        }
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public double getFramesPerSecond() {
        return framesPerSecond;
    }

    public void setFramesPerSecond(double framesPerSecond) {
        this.framesPerSecond = framesPerSecond;
    }

    public Identifier getResourceLocation() {
        return Identifier;
    }

    public File getMp4FileOnDisk() {
        return mp4FileOnDisk;
    }

    public int getLastFrame() {
        return lastFrame;
    }

    private static Path getVideoCacheFolder() {
        Path configPath = FabricLoader.getInstance().getGameDir();
        Path jsonPath = Paths.get(configPath.toAbsolutePath().toString(), "citadel/video_cache");
        if (!Files.exists(jsonPath)) {
            try {
                Files.createDirectories(jsonPath);
            } catch (Exception ignored) {
            }
        }
        return jsonPath;
    }

    private static BufferedImage toBufferedImage(Object src) {
        int croppedWidth = (int) invoke(src, "getCroppedWidth");
        int croppedHeight = (int) invoke(src, "getCroppedHeight");
        BufferedImage dst = new BufferedImage(croppedWidth, croppedHeight,
                BufferedImage.TYPE_3BYTE_BGR);
        toBufferedImage2(src, dst);
        return dst;
    }

    private static void toBufferedImage2(Object src, BufferedImage dst) {
        byte[] data = ((DataBufferByte) dst.getRaster().getDataBuffer()).getData();
        byte[] srcData = (byte[]) invoke(src, "getPlaneData", new Class[]{int.class}, new Object[]{0});
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (srcData[i] + 128);
        }
    }

    private static Object invoke(Object target, String methodName) {
        return invoke(target, methodName, new Class[0], new Object[0]);
    }

    private static Object invoke(Object target, String methodName, Class<?>[] paramTypes, Object[] args) {
        try {
            var method = target.getClass().getMethod(methodName, paramTypes);
            return method.invoke(target, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
