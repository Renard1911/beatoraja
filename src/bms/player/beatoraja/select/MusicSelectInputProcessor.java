package bms.player.beatoraja.select;

import bms.player.beatoraja.*;
import bms.player.beatoraja.input.BMSPlayerInputProcessor;
import bms.player.beatoraja.play.TargetProperty;
import bms.player.beatoraja.select.MusicSelectKeyProperty.MusicSelectKey;
import bms.player.beatoraja.select.bar.*;
import bms.player.beatoraja.song.SongData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Paths;

import static bms.player.beatoraja.select.MusicSelector.*;
import static bms.player.beatoraja.skin.SkinProperty.*;

import static bms.player.beatoraja.select.MusicSelectKeyProperty.MusicSelectKey.*;

/**
 * 選曲の入力処理用クラス
 *
 * @author exch
 */
public class MusicSelectInputProcessor {

    /**
     * バー移動中のカウンタ
     */
    private long duration;
    /**
     * バーの移動方向
     */
    private int angle;

    private final int durationlow = 300;
    private final int durationhigh = 50;

    private final MusicSelector select;

    public MusicSelectInputProcessor(MusicSelector select) {
        this.select = select;
    }

    public void input() {
        final BMSPlayerInputProcessor input = select.getMainController().getInputProcessor();
        final PlayerResource resource = select.getMainController().getPlayerResource();
        final PlayerConfig config = resource.getPlayerConfig();
        final BarRenderer bar = select.getBarRender();
        final Bar current = bar.getSelected();
        final long nowtime = select.getNowTime();

        boolean[] numberstate = input.getNumberState();
        long[] numtime = input.getNumberTime();
        if (numberstate[0] && numtime[0] != 0) {
            // 検索用ポップアップ表示。これ必要？
            numtime[0] = 0;
            Gdx.input.getTextInput(new Input.TextInputListener() {
                @Override
                public void input(String text) {
                    if (text.length() > 1) {
                        bar.addSearch(new SearchWordBar(select, text));
                        bar.updateBar(null);
                    }
                }

                @Override
                public void canceled() {
                }
            }, "Search", "", "Search bms title");
        }

        if (numberstate[1] && numtime[1] != 0) {
            // KEYフィルターの切り替え
            numtime[1] = 0;
            select.execute(MusicSelectCommand.NEXT_MODE);
        }
        if (numberstate[2] && numtime[2] != 0) {
            // ソートの切り替え
            numtime[2] = 0;
            select.execute(MusicSelectCommand.NEXT_SORT);
        }
        if (numberstate[3] && numtime[3] != 0) {
            // LNモードの切り替え
            numtime[3] = 0;
            select.execute(MusicSelectCommand.NEXT_LNMODE);
        }
        if (numberstate[4] && numtime[4] != 0) {
            // change replay
            numtime[4] = 0;
            select.execute(MusicSelectCommand.NEXT_REPLAY);
        }

        boolean[] keystate = input.getKeystate();
        long[] keytime = input.getTime();
        boolean[] cursor = input.getCursorState();
        long[] cursortime = input.getCursorTime();
        
        final MusicSelectKeyProperty property = MusicSelectKeyProperty.values()[config.getMusicselectinput()];

        if (input.startPressed()) {
            bar.resetInput();
            // show play option
            select.setPanelState(1);
            if (property.isPressed(keystate, keytime, OPTION1_DOWN, true)) {
                select.execute(MusicSelectCommand.NEXT_OPTION_1P);
            }
            if (property.isPressed(keystate, keytime, OPTION1_UP, true)) {
                config.setRandom((config.getRandom() + 9) % 10);
            }
            if (property.isPressed(keystate, keytime, GAUGE_DOWN, true)) {
                select.execute(MusicSelectCommand.NEXT_GAUGE_1P);
            }
            if (property.isPressed(keystate, keytime, GAUGE_UP, true)) {
                config.setGauge((config.getGauge() + 5) % 6);
            }
            if (property.isPressed(keystate, keytime, OPTIONDP_DOWN, true)) {
                select.execute(MusicSelectCommand.NEXT_OPTION_DP);
            }
            if (property.isPressed(keystate, keytime, OPTIONDP_UP, true)) {
                config.setDoubleoption((config.getDoubleoption() + 2) % 3);
            }
            if (property.isPressed(keystate, keytime, OPTION2_DOWN, true)) {
                select.execute(MusicSelectCommand.NEXT_OPTION_2P);
            }
            if (property.isPressed(keystate, keytime, OPTION2_UP, true)) {
                config.setRandom2((config.getRandom2() + 9) % 10);
            }
            if (property.isPressed(keystate, keytime, HSFIX_DOWN, true)) {
                select.execute(MusicSelectCommand.NEXT_HSFIX);
            }
            if (property.isPressed(keystate, keytime, HSFIX_UP, true)) {
                config.setFixhispeed((config.getFixhispeed() + 4) % 5);
            }

            // song bar scroll on mouse wheel
            int mov = -input.getScroll();
            input.resetScroll();
            // song bar scroll
            if (property.isPressed(keystate, keytime, TARGET_UP, false) || cursor[1]) {
                long l = System.currentTimeMillis();
                if (duration == 0) {
                    mov = 1;
                    duration = l + durationlow;
                    angle = durationlow;
                }
                if (l > duration) {
                    duration = l + durationhigh;
                    mov = 1;
                    angle = durationhigh;
                }
            } else if (property.isPressed(keystate, keytime, TARGET_DOWN, false) || cursor[0]) {
                long l = System.currentTimeMillis();
                if (duration == 0) {
                    mov = -1;
                    duration = l + durationlow;
                    angle = -durationlow;
                }
                if (l > duration) {
                    duration = l + durationhigh;
                    mov = -1;
                    angle = -durationhigh;
                }
            } else {
                long l = System.currentTimeMillis();
                if (l > duration) {
                    duration = 0;
                }
            }

            TargetProperty[] targets = TargetProperty.getAllTargetProperties();
            while(mov > 0) {
                config.setTarget((config.getTarget() + 1) % targets.length);
                select.play(SOUND_SCRATCH);
                mov--;
            }
            while(mov < 0) {
                config.setTarget((config.getTarget() + targets.length - 1) % targets.length);
                select.play(SOUND_SCRATCH);
                mov++;
            }
        } else if (input.isSelectPressed()) {
            bar.resetInput();
            // show assist option
            select.setPanelState(2);
            if (property.isPressed(keystate, keytime, JUDGEWINDOW_UP, true)) {
                config.setJudgewindowrate(config.getJudgewindowrate() == 100 ? 400 : 100);
            }
            if (property.isPressed(keystate, keytime, CONSTANT, true)) {
                config.setConstant(!config.isConstant());
            }
            if (property.isPressed(keystate, keytime, JUDGEAREA, true)) {
                config.setShowjudgearea(!config.isShowjudgearea());
            }
            if (property.isPressed(keystate, keytime, LEGACYNOTE, true)) {
                config.setLegacynote(!config.isLegacynote());
            }
            if (property.isPressed(keystate, keytime, MARKNOTE, true)) {
                config.setMarkprocessednote(!config.isMarkprocessednote());
            }
            if (property.isPressed(keystate, keytime, BPMGUIDE, true)) {
                config.setBpmguide(!config.isBpmguide());
            }
            if (property.isPressed(keystate, keytime, NOMINE, true)) {
                config.setNomine(!config.isNomine());
            }
        } else if (input.getNumberState()[5]) {
            bar.resetInput();
            // show detail option
            select.setPanelState(3);
            PlayConfig pc = null;
            if (current instanceof SongBar && ((SongBar)current).existsSong()) {
                SongBar song = (SongBar) current;
                pc = config.getPlayConfig(song.getSongData().getMode());
            }
            if (property.isPressed(keystate, keytime, BGA_DOWN, true)) {
                resource.getConfig().setBga((resource.getConfig().getBga() + 1) % 3);
            }
            if (property.isPressed(keystate, keytime, DURATION_DOWN, true)) {
                if (pc != null && pc.getDuration() > 1) {
                    pc.setDuration(pc.getDuration() - 1);
                }
            }
            if (property.isPressed(keystate, keytime, JUDGETIMING_DOWN, true)) {
                if (config.getJudgetiming() > -99) {
                    config.setJudgetiming(config.getJudgetiming() - 1);
                }
            }
            if (property.isPressed(keystate, keytime, DURATION_UP, true)) {
                if (pc != null && pc.getDuration() < 2000) {
                    pc.setDuration(pc.getDuration() + 1);
                }
            }
            if (property.isPressed(keystate, keytime, JUDGETIMING_UP, true)) {
                if (config.getJudgetiming() < 99) {
                    config.setJudgetiming(config.getJudgetiming() + 1);
                }
            }
        } else {
            bar.input();
            select.setPanelState(0);

            if (current instanceof SelectableBar) {
                if (property.isPressed(keystate, keytime, PLAY, true) || (cursor[3] && cursortime[3] != 0)) {
                    // play
                    cursortime[3] = 0;
                    select.selectSong(0);
                } else if (property.isPressed(keystate, keytime, PRACTICE, true)) {
                    // practice mode
                    select.selectSong(2);
                } else if (property.isPressed(keystate, keytime, AUTO, true)) {
                    // auto play
                    select.selectSong(1);
                } else if (property.isPressed(keystate, keytime, MusicSelectKey.REPLAY, true)) {
                    // replay
                    select.selectSong(3);
                }
            } else {
                if (property.isPressed(keystate, keytime, FOLDER_OPEN, true) || (cursor[3] && cursortime[3] != 0)) {
                    // open folder
                    cursortime[3] = 0;
                    if (bar.updateBar(current)) {
                        select.play(SOUND_FOLDEROPEN);
                    }
                }
            }

            if (numberstate[7] && numtime[7] != 0) {
                numtime[7] = 0;
                select.execute(MusicSelectCommand.NEXT_RIVAL);
            }
            if (numberstate[8] && numtime[8] != 0) {
                numtime[8] = 0;
                if (current instanceof SongBar && ((SongBar) current).existsSong() && 
                        (bar.getDirectory().isEmpty() || !(bar.getDirectory().getLast() instanceof SameFolderBar))) {
                    SongData sd = ((SongBar) current).getSongData();
                    bar.updateBar(new SameFolderBar(select, sd.getTitle(), sd.getFolder()));
                }
            }
            if (numberstate[9] && numtime[9] != 0) {
                numtime[9] = 0;
                select.execute(MusicSelectCommand.OPEN_DOCUMENT);
            }
            // close folder
            if (property.isPressed(keystate, keytime, FOLDER_CLOSE, true) || (cursor[2] && cursortime[2] != 0)) {
                keytime[1] = 0;
                cursortime[2] = 0;
                bar.close();
            }
        }

        // song bar moved
        if (bar.getSelected() != current) {
            select.selectedBarMoved();
        }
        if(select.getTimer()[TIMER_SONGBAR_CHANGE] == Long.MIN_VALUE) {
            select.getTimer()[TIMER_SONGBAR_CHANGE] = nowtime;
        }
        // update folder
        if (input.getFunctionstate()[1] && input.getFunctiontime()[1] != 0) {
            input.getFunctiontime()[1] = 0;
            select.updateSong(current);
        }
        // open explorer with selected song
        if (input.getFunctionstate()[2] && input.getFunctiontime()[2] != 0) {
            input.getFunctiontime()[2] = 0;
            select.execute(MusicSelectCommand.OPEN_WITH_EXPLORER);
        }

        if (input.isExitPressed()) {
            select.getMainController().exit();
        }
    }
}
