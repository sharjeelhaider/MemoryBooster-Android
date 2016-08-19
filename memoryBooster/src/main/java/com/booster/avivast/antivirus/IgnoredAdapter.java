package com.booster.avivast.antivirus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.List;

import com.booster.avivast.R;

/**
 * Created by Magic Frame on 27/01/2016.
 */
public class IgnoredAdapter  extends ArrayAdapter<IProblem>
{
    private final Context _context;
    private List<IProblem> _values =null;
    IOnAdapterItemRemoved _adapterListener=null;
    void setOnAdapterItemRemovedListener(IOnAdapterItemRemoved listener) { _adapterListener=listener;}

    public IgnoredAdapter(Context context, List<IProblem> values)
    {
        super(context, R.layout.ignored_list_item, values);
        this._context = context;
        this._values = values;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {

        final View rowView;

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.ignored_list_item, parent, false);
        }else
        {
            rowView = convertView;

        }

        final IProblem obj = _values.get(position);


        final String dialogMessage=null;


        final TextView textView = (TextView) rowView.findViewById(R.id.nameAppIgnored);
        final ImageView imageView = (ImageView) rowView.findViewById(R.id.iconAppIgnored);
        final LinearLayout linearLayout = (LinearLayout) rowView.findViewById(R.id.linearLayoutAdapter);
        linearLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(_context.getString(R.string.warning))
                        .setPositiveButton(_context.getString(R.string.accept_eula), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                                remove(obj);
                                _adapterListener.onItemRemoved(obj);
                            }
                        }).setNegativeButton(_context.getString(R.string.cancel), new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });

                String dialogMessage = null;

                if (obj.getType() == IProblem.ProblemType.AppProblem)
                {
                    AppProblem appProblem = (AppProblem) obj;
                    dialogMessage = _context.getString(R.string.remove_ignored_app_message) + " " + StaticTools.getAppNameFromPackage(getContext(), appProblem.getPackageName());
                }
                else
                {
                    SystemProblem sp=((SystemProblem) obj);
                    dialogMessage = sp.getWhiteListOnRemoveDescription(_context);
                }

                builder.setMessage(dialogMessage);
                builder.show();

            }
        });

        if (obj.getType() == IProblem.ProblemType.AppProblem)
        {
            AppProblem appProblem = (AppProblem) obj;
            textView.setText(StaticTools.getAppNameFromPackage(getContext(), appProblem.getPackageName()));
            imageView.setImageDrawable(StaticTools.getIconFromPackage(appProblem.getPackageName(), getContext()));
        } else
        {
            SystemProblem sp=((SystemProblem) obj);
            textView.setText(sp.getTitle(getContext()));
            imageView.setImageDrawable(sp.getIcon(getContext()));
        }

        return rowView;
    }

    public void refresh(List<IProblem> bpd)
    {
        clear();
        addAll(bpd);
    }
}
