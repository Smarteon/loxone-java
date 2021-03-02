package cz.smarteon.loxone.app;

import org.jetbrains.annotations.NotNull;

import cz.smarteon.loxone.LoxoneUuid;

public class AudioZoneControl extends Control {

    public static final String NAME = "MediaClient";

    @NotNull
    public LoxoneUuid stateServerState() {
        return getCompulsoryState("serverState").only();
    }
    @NotNull
    public LoxoneUuid statePlayState() {
        return getCompulsoryState("playState").only();
    }
    @NotNull
    public LoxoneUuid stateClientState() {
        return getCompulsoryState("clientState").only();
    }
    @NotNull
    public LoxoneUuid statePower() {
        return getCompulsoryState("power").only();
    }
    @NotNull
    public LoxoneUuid stateVolume() {
        return getCompulsoryState("volume").only();
    }
    @NotNull
    public LoxoneUuid stateMaxVolume() {
        return getCompulsoryState("● maxVolume").only();
    }
    @NotNull
    public LoxoneUuid stateVolumeStep() {
        return getCompulsoryState("volumeStep").only();
    }
    @NotNull
    public LoxoneUuid stateShuffle() {
        return getCompulsoryState("shuffle").only();
    }
    @NotNull
    public LoxoneUuid stateSourceList() {
        return getCompulsoryState("sourceList").only();
    }
    @NotNull
    public LoxoneUuid stateRepeat() {
        return getCompulsoryState("repeat").only();
    }
    @NotNull
    public LoxoneUuid stateSongName() {
        return getCompulsoryState("songName").only();
    }
    @NotNull
    public LoxoneUuid stateDuration() {
        return getCompulsoryState("duration").only();
    }
    @NotNull
    public LoxoneUuid stateProgress() {
        return getCompulsoryState("progress").only();
    }
    @NotNull
    public LoxoneUuid stateAlbum() {
        return getCompulsoryState("album").only();
    }
    @NotNull
    public LoxoneUuid stateArtist() {
        return getCompulsoryState("artist").only();
    }
    @NotNull
    public LoxoneUuid stateStation() {
        return getCompulsoryState("station").only();
    }
    @NotNull
    public LoxoneUuid stateGenre() {
        return getCompulsoryState("genre").only();
    }
    @NotNull
    public LoxoneUuid stateCover() {
        return getCompulsoryState("cover").only();
    }
    @NotNull
    public LoxoneUuid stateSource() {
        return getCompulsoryState("source").only();
    }
    @NotNull
    public LoxoneUuid stateQueueIndex() {
        return getCompulsoryState("queueIndex").only();
    }
    @NotNull
    public LoxoneUuid stateEnableAirPlay() {
        return getCompulsoryState("enableAirPlay").only();
    }
    @NotNull
    public LoxoneUuid stateEnableSpotifyConnect() {
        return getCompulsoryState("enableSpotifyConnect").only();
    }
    @NotNull
    public LoxoneUuid stateAlarmVolume() {
        return getCompulsoryState("alarmVolume").only();
    }
    @NotNull
    public LoxoneUuid stateBellVolume() {
        return getCompulsoryState("bellVolume").only();
    }
    @NotNull
    public LoxoneUuid stateBuzzerVolume() {
        return getCompulsoryState("buzzerVolume").only();
    }
    @NotNull
    public LoxoneUuid stateTtsVolume() {
        return getCompulsoryState("ttsVolume").only();
    }
    @NotNull
    public LoxoneUuid stateDefaultVolume() {
        return getCompulsoryState("defaultVolume").only();
    }
    @NotNull
    public LoxoneUuid stateEqualizerSettings() {
        return getCompulsoryState("equalizerSettings").only();
    }
    @NotNull
    public LoxoneUuid stateMastervolume() {
        return getCompulsoryState("mastervolume").only();
    }
}
