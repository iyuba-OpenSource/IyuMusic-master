package com.iyuba.music.data.local;

import com.iyuba.music.data.model.VoaText;

import java.util.Collection;
import java.util.List;

import io.reactivex.Observable;

public interface VoaTextTableInter {

    String TABLE_NAME = "voaText";

    String COLUMN_VOA_ID = "voaId";
    String COLUMN_PARA_ID = "paraId";
    String COLUMN_ID_INDEX = "idIndex";
    String COLUMN_SENTENCE_CN = "sentenceCn";
    String COLUMN_SENTENCE = "sentence";
    String COLUMN_IMG_WORDS = "imgWords";
    String COLUMN_IMG_PATH = "imgPath";
    String COLUMN_TIMING = "timing";
    String COLUMN_END_TIMING = "endTiming";

    void setVoaTexts(Collection<VoaText> voaTexts, final int voaId);

    Observable<List<VoaText>> getVoaTexts(final int voaId);
}
