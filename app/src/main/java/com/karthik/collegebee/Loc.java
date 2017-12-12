package com.karthik.collegebee;

import android.support.annotation.Keep;

/**
 * Created by guest1 on 12/10/2017.
 */

public class Loc {
        double lat;
        double lng;
     @Keep
    public Loc() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
@Keep
    public Loc(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }
    }

