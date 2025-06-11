package com.iyuba.assessment;

import android.os.Bundle;

public interface EvaluatorListener {
    void onVolumeChanged(int var1, byte[] var2);

    void onBeginOfSpeech();

    void onEndOfSpeech();

    void onResult(EvaluatorResult var1, boolean var2);

    void onError(SpeechError var1);

    void onEvent(int var1, int var2, int var3, Bundle var4);
}
