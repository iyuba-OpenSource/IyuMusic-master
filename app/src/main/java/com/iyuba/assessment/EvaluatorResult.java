package com.iyuba.assessment;

import android.os.Parcel;
import android.os.Parcelable;

public class EvaluatorResult implements Parcelable {
    private String a;
    public static final Creator<EvaluatorResult> CREATOR = new Creator<EvaluatorResult>() {
        @Override
        public EvaluatorResult createFromParcel(Parcel source) {
            return null;
        }

        @Override
        public EvaluatorResult[] newArray(int size) {
            return new EvaluatorResult[0];
        }

        public EvaluatorResult a(Parcel var1) {
            return new EvaluatorResult(var1);
        }

        public EvaluatorResult[] a(int var1) {
            return new EvaluatorResult[var1];
        }
    };

    private EvaluatorResult(Parcel var1) {
        this.a = "";
        this.a = var1.readString();
    }

    public EvaluatorResult(String var1) {
        this.a = "";
        if (null != var1) {
            this.a = var1;
        }

    }

    public String getResultString() {
        return this.a;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel var1, int var2) {
        var1.writeString(this.a);
    }
}
