package au.csiro.gsnlite.beans;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class StreamElement4Plugins implements Serializable, Parcelable {

	Serializable value;
	int length;

	public StreamElement4Plugins(Serializable v) {
		this.value = v;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Serializable getValue() {
		return value;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeSerializable(value);

	}

	public static final Parcelable.Creator<StreamElement4Plugins> CREATOR = new Parcelable.Creator<StreamElement4Plugins>() {
		public StreamElement4Plugins createFromParcel(Parcel in) {
			return new StreamElement4Plugins(in);
		}

		public StreamElement4Plugins[] newArray(int size) {
			return new StreamElement4Plugins[size];
		}
	};

	private StreamElement4Plugins(Parcel in) {
		readFromParcel(in);
	}

	private void readFromParcel(Parcel in) {
		this.value = in.readSerializable();
	}
}
