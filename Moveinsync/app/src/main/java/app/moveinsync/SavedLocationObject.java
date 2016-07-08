package app.moveinsync;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by bharath on 10/5/15.
 */
@DatabaseTable
public class SavedLocationObject {

    @DatabaseField
    private String name;

    @DatabaseField
    private String latitude;

    @DatabaseField
    private String longitude;


    public SavedLocationObject() {

    }

    public SavedLocationObject(String name, String latitude, String longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
