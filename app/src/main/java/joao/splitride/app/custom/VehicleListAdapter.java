package joao.splitride.app.custom;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.adapters.ArraySwipeAdapter;

import java.util.List;

import joao.splitride.R;
import joao.splitride.app.entities.Vehicle;

/**
 * Created by Joao on 15-08-2016.
 */
public class VehicleListAdapter<T> extends ArraySwipeAdapter {
    private List<Vehicle> items;
    private int layoutResourceId;
    private Context context;

    public VehicleListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public VehicleListAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public VehicleListAdapter(Context context, int resource, Object[] objects) {
        super(context, resource, objects);
    }

    public VehicleListAdapter(Context context, int resource, int textViewResourceId, Object[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public VehicleListAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    public VehicleListAdapter(Context context, int resource, int textViewResourceId, List objects) {
        super(context, resource, textViewResourceId, objects);

        this.layoutResourceId = resource;
        this.context = context;
        this.items = objects;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        VehicleHolder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder = new VehicleHolder();
        holder.vehicle = items.get(position);
        holder.edit = (ImageView) row.findViewById(R.id.edit);
        holder.remove = (ImageView) row.findViewById(R.id.delete);
        holder.edit.setTag(holder.vehicle);
        holder.remove.setTag(holder.vehicle);

        holder.name = (TextView) row.findViewById(R.id.line_name);

        row.setTag(holder);

        setupItem(holder);

        return row;
    }

    private void setupItem(VehicleHolder holder) {
        holder.name.setText(holder.vehicle.getVehicleName());
    }

    public static class VehicleHolder {
        Vehicle vehicle;
        TextView name;
        ImageView edit, remove;
    }
}
