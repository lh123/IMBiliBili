package com.lh.imbilibili.view.adapter.categoryfragment.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuhui on 2016/10/1.
 */

public class PartionModel implements Parcelable {
    private int id;
    private String name;
    private List<Partion> partions;

    protected PartionModel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        partions = in.createTypedArrayList(Partion.CREATOR);
    }

    private PartionModel() {
        partions = new ArrayList<>();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeTypedList(partions);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PartionModel> CREATOR = new Creator<PartionModel>() {
        @Override
        public PartionModel createFromParcel(Parcel in) {
            return new PartionModel(in);
        }

        @Override
        public PartionModel[] newArray(int size) {
            return new PartionModel[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Partion> getPartions() {
        return partions;
    }

    public void setPartions(List<Partion> partions) {
        this.partions = partions;
    }

    public static class Partion implements Parcelable {
        private int imgId;
        private String name;
        private int id;

        Partion() {
        }

        Partion(Parcel in) {
            imgId = in.readInt();
            name = in.readString();
            id = in.readInt();
        }

        public static final Creator<Partion> CREATOR = new Creator<Partion>() {
            @Override
            public Partion createFromParcel(Parcel in) {
                return new Partion(in);
            }

            @Override
            public Partion[] newArray(int size) {
                return new Partion[size];
            }
        };

        public int getImgId() {
            return imgId;
        }

        void setImgId(int imgId) {
            this.imgId = imgId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(imgId);
            dest.writeString(name);
            dest.writeInt(id);
        }
    }

    public static class Builder {
        private PartionModel partionModel;

        public Builder() {
            this.partionModel = new PartionModel();
        }

        public Builder setId(int id) {
            partionModel.setId(id);
            return this;
        }

        public Builder setName(String name) {
            partionModel.setName(name);
            return this;
        }

        public Builder addSubPartion(String name, int imgId, int id) {
            Partion partion = new Partion();
            partion.setName(name);
            partion.setImgId(imgId);
            partion.setId(id);
            partionModel.getPartions().add(partion);
            return this;
        }

        public PartionModel build() {
            return partionModel;
        }
    }
}
