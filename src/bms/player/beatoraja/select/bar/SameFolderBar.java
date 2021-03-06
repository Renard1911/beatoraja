package bms.player.beatoraja.select.bar;

import bms.player.beatoraja.select.MusicSelector;
import bms.player.beatoraja.song.SongData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by exch on 2017/09/03.
 */
public class SameFolderBar extends DirectoryBar {

    private MusicSelector selector;
    private String crc;
    private String title;

    public SameFolderBar(MusicSelector selector, String title, String crc) {
        this.selector = selector;
        this.crc = crc;
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Bar[] getChildren() {
        List<Bar> l = new ArrayList<Bar>();
        SongData[] songs = selector.getSongDatabase().getSongDatas("folder", crc);
        List<String> sha = new ArrayList<String>();
        for (SongData song : songs) {
            if(!sha.contains(song.getSha256())) {
                l.add(new SongBar(song));
                sha.add(song.getSha256());
            }
        }
        return l.toArray(new Bar[0]);
    }
}
