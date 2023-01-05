package effectivejavapart2.study.chapter04.item20.typeframework;

public interface SingerSongwriter extends Singer, Songwriter{

    AudioClip strum();
    void actSensitive();
}
