package sapphire.appexamples.wordscramblewithfriends.app;

import sapphire.app.SapphireObject;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

public class LocationService implements SapphireObject {
	private static final long MIN_UPDATE_TIME = 1000; // in ms
	private static final float MIN_UPDATE_DISTANCE = 400; // in meters

	private LocationManager locationManager;
	private LocationListener locationListener;
	private Location lastLocation;
	private boolean isActive;

	public void initialize(Context context) {
		Looper.prepare();
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		locationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				if (location != null)
					lastLocation = location;
			}

			@Override
			public void onProviderDisabled(String provider) {
			}

			@Override
			public void onProviderEnabled(String provider) {
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
			}
		};
		activateLocationService();
		isActive = true;
	}

	public Location getLocation() {
		if (!isActive) {
			return getLastCachedLocation();
		}
		return lastLocation;
	}

	public void activateLocationService() {
		lastLocation = getLastCachedLocation();
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, MIN_UPDATE_TIME,
				MIN_UPDATE_DISTANCE, locationListener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				MIN_UPDATE_TIME, MIN_UPDATE_DISTANCE, locationListener);
		isActive = true;
	}

	public void deactivateLocationService() {
		locationManager.removeUpdates(locationListener);
		isActive = false;
	}

	private Location getLastCachedLocation() {
		Location lastKnownLocation = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (lastKnownLocation == null) {
			lastKnownLocation = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		return lastKnownLocation;
	}
}
