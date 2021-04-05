package com.nexenio.bleindoorpositioningdemo.ui.beaconview.radar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import androidx.annotation.Nullable;

import android.os.StrictMode;
import android.util.AttributeSet;

import com.nexenio.bleindoorpositioning.ble.advertising.AdvertisingPacket;
import com.nexenio.bleindoorpositioning.ble.beacon.Beacon;
import com.nexenio.bleindoorpositioning.location.Location;
import com.nexenio.bleindoorpositioning.location.LocationListener;
import com.nexenio.bleindoorpositioning.location.distance.DistanceUtil;
import com.nexenio.bleindoorpositioning.location.provider.LocationProvider;
import com.nexenio.bleindoorpositioningdemo.login.ui.main.LoginDataFragment;
import com.nexenio.bleindoorpositioningdemo.ui.LocationAnimator;
import com.nexenio.bleindoorpositioningdemo.ui.beaconview.BeaconView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.nexenio.bleindoorpositioningdemo.HomeActivity;
import com.nexenio.bleindoorpositioningdemo.UrlLinks;
import com.nexenio.bleindoorpositioningdemo.jSOnClassforData;
/**
 * Created by steppschuh on 16.11.17.
 */

public class BeaconRadar extends BeaconView {

    /*
        Device drawing related variables
     */
    protected ValueAnimator deviceAngleAnimator;
    protected ValueAnimator deviceAccuracyAnimator;
    protected float deviceAccuracyAnimationValue;
    protected float deviceAdvertisingRange;
    protected float deviceAdvertisingRadius;
    protected float deviceStrokeRadius;

    /*
        Beacon drawing related variables
     */
    protected float beaconAccuracyAnimationValue;
    protected float beaconRadius = pixelsPerDip * 8;
    protected float beaconCornerRadius = pixelsPerDip * 2;
    protected float beaconStrokeRadius;
    protected long timeSinceLastAdvertisement;

    /*
        Legend drawing related variables
     */
    protected Paint legendPaint;
    protected int referenceLineCount = 5;
    protected float referenceDistance;
    protected float referenceDistanceStep;
    protected float currentReferenceDistance;
    protected float currentReferenceCanvasUnits;
    protected String referenceText;
    protected float referenceTextWidth;

    /*
        Location mapping related variables
     */
    protected ValueAnimator maximumDistanceAnimator;
    protected double locationDistance;
    protected double locationRadius;
    protected double locationRotationAngle;

    public BeaconRadar(Context context) {
        super(context);
    }

    public BeaconRadar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BeaconRadar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BeaconRadar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initialize() {
        super.initialize();
        startMaximumDistanceAnimation(100);
        startDeviceAngleAnimation(0);
        legendPaint = new Paint(textPaint);
        legendPaint.setTextSize(pixelsPerDip * 12);
        legendPaint.setStyle(Paint.Style.STROKE);
        legendPaint.setColor(Color.BLACK);
        legendPaint.setAlpha(50);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLegend(canvas);
    }

    @Override
    protected void drawDevice(Canvas canvas) {
        deviceAdvertisingRange = 20; // in meters TODO: get real value based on tx power
        deviceAdvertisingRadius = getCanvasUnitsFromMeters(deviceAdvertisingRange);

        deviceAccuracyAnimationValue = (deviceAccuracyAnimator == null) ? 0 : (float) deviceAccuracyAnimator.getAnimatedValue();
        deviceStrokeRadius = (pixelsPerDip * 10) + (pixelsPerDip * 2 * deviceAccuracyAnimationValue);

        canvas.drawCircle(canvasCenter.x, canvasCenter.y, deviceStrokeRadius, whiteFillPaint);
        canvas.drawCircle(canvasCenter.x, canvasCenter.y, deviceStrokeRadius, secondaryStrokePaint);
        canvas.drawCircle(canvasCenter.x, canvasCenter.y, pixelsPerDip * 8, secondaryFillPaint);
    }

    @Override
    protected void drawBeacons(Canvas canvas) throws JSONException {
        Map<Beacon, PointF> beaconCenterMap = new HashMap<>();
        // draw all backgrounds
        for (Beacon beacon : beacons) {
            PointF beaconCenter = getPointFromLocation(beacon.getLocation(), beacon);
            beaconCenterMap.put(beacon, beaconCenter);
            drawBeaconBackground(canvas, beacon, beaconCenter);
        }
        // draw all foregrounds
        for (Beacon beacon : beacons) {
            drawBeaconForeground(canvas, beacon, beaconCenterMap.get(beacon));
        }
    }

    /**
     * This shouldn't be called, because the created beacon background may overlay existing beacon
     * foregrounds. Use {@link #drawBeacons(Canvas)} instead.
     */
    @Override
    protected void drawBeacon(Canvas canvas, Beacon beacon) throws JSONException {
        PointF beaconCenter = getPointFromLocation(beacon.getLocation(), beacon);
        drawBeaconBackground(canvas, beacon, beaconCenter);
        drawBeaconForeground(canvas, beacon, beaconCenter);
    }

    protected void drawBeaconBackground(Canvas canvas, Beacon beacon, PointF beaconCenter) {

    }

    protected void drawBeaconForeground(Canvas canvas, Beacon beacon, PointF beaconCenter) throws JSONException {
        AdvertisingPacket latestAdvertisingPacket = beacon.getLatestAdvertisingPacket();
        String distance=String.valueOf(beacon.getDistance());
        timeSinceLastAdvertisement = latestAdvertisingPacket != null ? System.currentTimeMillis() - latestAdvertisingPacket.getTimestamp() : 0;

        beaconAccuracyAnimationValue = (deviceAccuracyAnimator == null) ? 0 : (float) deviceAccuracyAnimator.getAnimatedValue();
        beaconAccuracyAnimationValue *= Math.max(0, 1 - (timeSinceLastAdvertisement / 1000));
        beaconStrokeRadius = beaconRadius + (pixelsPerDip * 2) + (pixelsPerDip * 2 * beaconAccuracyAnimationValue);

        String username= LoginDataFragment.usernameglobal;

        RectF rect = new RectF(beaconCenter.x - beaconStrokeRadius, beaconCenter.y - beaconStrokeRadius, beaconCenter.x + beaconStrokeRadius, beaconCenter.y + beaconStrokeRadius);
        canvas.drawRoundRect(rect, beaconCornerRadius, beaconCornerRadius, whiteFillPaint);
        canvas.drawRoundRect(rect, beaconCornerRadius, beaconCornerRadius, primaryStrokePaint);
        canvas.drawText(username+"@"+distance, beaconCenter.x - beaconStrokeRadius, beaconCenter.y - beaconStrokeRadius, primaryFillPaint);
        System.out.println(rect);
//        rect = new RectF(beaconCenter.x - beaconRadius, beaconCenter.y - beaconRadius, beaconCenter.x + beaconRadius, beaconCenter.y + beaconRadius);
//        canvas.drawRoundRect(rect, beaconCornerRadius, beaconCornerRadius, primaryFillPaint);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String url1 = UrlLinks.insertothersdata;
        List<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>(6);
        nameValuePairs1.add(new BasicNameValuePair("username", username));
        nameValuePairs1.add(new BasicNameValuePair("a", String.valueOf(beaconCenter.x - beaconRadius)));
        nameValuePairs1.add(new BasicNameValuePair("b",  String.valueOf(beaconCenter.y - beaconRadius)));
        nameValuePairs1.add(new BasicNameValuePair("c",  String.valueOf( beaconCenter.x + beaconRadius)));
        nameValuePairs1.add(new BasicNameValuePair("d",  String.valueOf(beaconCenter.y + beaconRadius)));
        nameValuePairs1.add(new BasicNameValuePair("distance",  distance));
//		jsonObj = jSOnClassforData.forCallingString(url);
        jSOnClassforData.forCallingServer(url1, nameValuePairs1);




        System.out.println(rect);




        try {

            String url= UrlLinks.urlserver+"gettingUserData?username="+username;
            JSONObject jsonObj = jSOnClassforData.forCallingString(url);

            JSONArray jArray = null;

            try {
                jArray = jsonObj.getJSONArray("jsonarrayval");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            System.out.println("*****JARRAY*****"+jArray.length());

            for(int i=0;i<jArray.length();i++){


                JSONObject json_data;
                try {
                    json_data = jArray.getJSONObject(i);


                    String bookName=json_data.getString("bookName");
                    String a=json_data.getString("a");
                    String b=json_data.getString("b");
                    String c=json_data.getString("c");
                    String d=json_data.getString("d");
                    String distance12=json_data.getString("distance");
                    float a1= Float.parseFloat(a);
                    float b1= Float.parseFloat(b);
                    float c1= Float.parseFloat(c);
                    float d1= Float.parseFloat(d);
                    float distance1= Float.parseFloat(distance12);
                    //   dbHelper.createEventdetails(bookName,author,publisher,stock);
                    rect = new RectF(a1, b1, c1, d1);
                    canvas.drawRoundRect(rect, beaconCornerRadius, beaconCornerRadius, primaryFillPaint);
                    canvas.drawText(bookName+"@"+distance1, a1 - beaconStrokeRadius, b1 - beaconStrokeRadius, primaryFillPaint);


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



    }

    protected void drawLegend(Canvas canvas) {
        referenceDistance = DistanceUtil.getReasonableSmallerEvenDistance((float) maximumDistanceAnimator.getAnimatedValue());
        referenceDistanceStep = Math.round(referenceDistance / (float) referenceLineCount);

        // include some more (+ 5) lines that are needed to avoid white space
        for (int i = referenceLineCount + 5; i > 0; i--) {
            currentReferenceDistance = Math.round(i * referenceDistanceStep);
            currentReferenceCanvasUnits = getCanvasUnitsFromMeters(currentReferenceDistance);

            canvas.drawCircle(
                    canvasCenter.x,
                    canvasCenter.y,
                    currentReferenceCanvasUnits,
                    legendPaint
            );

            referenceText = String.format(Locale.US, "%.0f", currentReferenceDistance) + "m";
            referenceTextWidth = legendPaint.measureText(referenceText);
            canvas.drawText(
                    referenceText,
                    canvasCenter.x - (referenceTextWidth / 2),
                    canvasCenter.y + currentReferenceCanvasUnits + legendPaint.getTextSize(),
                    legendPaint
            );
        }

    }

    protected PointF getPointFromLocation(Location location) {
        return getPointFromLocation(location, null);
    }

    protected PointF getPointFromLocation(Location location, @Nullable Beacon beacon) {
//        if (deviceLocationAnimator == null) {
//            return new PointF(canvasCenter.x, canvasCenter.y);
//        }
        locationDistance = beacon != null ? beacon.getDistance() : location.getDistanceTo(deviceLocationAnimator.getLocation());
        locationRadius = getCanvasUnitsFromMeters(locationDistance);
        locationRotationAngle =1025;

        // locationRotationAngle=deviceLocationAnimator.getLocation().getAngleTo(location);

        deviceLocationAnimator = startLocationAnimation(deviceLocationAnimator, deviceLocation, new LocationListener() {
            @Override
            public void onLocationUpdated(LocationProvider locationProvider, Location location) {
                onLocationsChanged();
            }
        });
        // locationRotationAngle=deviceLocationAnimator.getLocation().getAngleTo(location);
        locationRotationAngle = (locationRotationAngle - (float) deviceAngleAnimator.getAnimatedValue()) % 360;
        locationRotationAngle = Math.toRadians(locationRotationAngle) - (Math.PI / 2);
        return new PointF(
                (float) (canvasCenter.x + (locationRadius * Math.cos(locationRotationAngle))),
                (float) (canvasCenter.y + (locationRadius * Math.sin(locationRotationAngle)))
        );


    }

    protected float getCanvasUnitsFromMeters(double meters) {
        return (float) (Math.min(canvasCenter.x, canvasCenter.y) * meters) / (float) maximumDistanceAnimator.getAnimatedValue();
    }

    protected float getMetersFromCanvasUnits(float canvasUnits) {
        return ((float) maximumDistanceAnimator.getAnimatedValue() * canvasUnits) / Math.min(canvasCenter.x, canvasCenter.y);
    }

    public void fitToCurrentLocations() {
        float maximumDistance = 10;
        // TODO: get actual maximum distance
        startMaximumDistanceAnimation(maximumDistance);
    }

    @Override
    public void onDeviceLocationChanged() {
        startDeviceRadiusAnimation();
        super.onDeviceLocationChanged();
    }





    protected void startMaximumDistanceAnimation(float distance) {
        float originValue = distance;
        if (maximumDistanceAnimator != null) {
            originValue = (float) maximumDistanceAnimator.getAnimatedValue();
            maximumDistanceAnimator.cancel();
        }
        maximumDistanceAnimator = ValueAnimator.ofFloat(originValue, distance);
        maximumDistanceAnimator.setDuration(LocationAnimator.ANIMATION_DURATION_LONG);
        maximumDistanceAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                invalidate();
            }
        });
        maximumDistanceAnimator.start();
    }


    protected void startDeviceRadiusAnimation() {
        if (deviceAccuracyAnimator != null && deviceAccuracyAnimator.isRunning()) {
            return;
        }
        deviceAccuracyAnimator = ValueAnimator.ofFloat(0, 1);
        deviceAccuracyAnimator.setDuration(LocationAnimator.ANIMATION_DURATION_LONG);
        deviceAccuracyAnimator.setRepeatCount(1);
        deviceAccuracyAnimator.setRepeatMode(ValueAnimator.REVERSE);
        deviceAccuracyAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                invalidate();
            }
        });
        deviceAccuracyAnimator.start();
    }

    public void startDeviceAngleAnimation(float deviceAngle) {
        float originValue = deviceAngle;
        if (deviceAngleAnimator != null) {
            originValue = (float) deviceAngleAnimator.getAnimatedValue();
            deviceAngleAnimator.cancel();
        }
        deviceAngleAnimator = ValueAnimator.ofFloat(originValue, deviceAngle);
        deviceAngleAnimator.setDuration(200);
        deviceAngleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                invalidate();
            }
        });
        deviceAngleAnimator.start();
    }

}
