package pl.aptewicz.nodemaps.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import pl.aptewicz.nodemaps.R;
import pl.aptewicz.nodemaps.model.FtthIssue;

public class FtthIssueAdapter extends ArrayAdapter<FtthIssue> {

    private final Context context;
    private final int layoutResourceId;
    private final FtthIssue[] data;

    public FtthIssueAdapter(Context context, int layoutResourceId, FtthIssue[] data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        FtthJobHolder ftthJobHolder;

        if (row == null) {
            LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
            row = layoutInflater.inflate(layoutResourceId, parent, false);

            ftthJobHolder = new FtthJobHolder();
            ftthJobHolder.textView = (TextView) row.findViewById(R.id.ftthJobDescription);

            row.setTag(ftthJobHolder);
        } else {
            ftthJobHolder = (FtthJobHolder) row.getTag();
        }

        FtthIssue ftthIssue = data[position];
        ftthJobHolder.textView.setText(context.getString(R.string.ftth_job_description) + "\n"
                + ftthIssue.getDescription() + "\n\n" + context.getString(R.string.latitude) + "\n"
                + ftthIssue.getLatitude() + "\n\n" + context.getString(R.string.longitude) + "\n"
                + ftthIssue.getLongitude() + "\n\n" + context.getString(R.string.ftth_job_status) + "\n"
            + ftthIssue.getFtthJob().getJobStatus());

        return row;
    }

    private static class FtthJobHolder {
        private TextView textView;
    }
}
