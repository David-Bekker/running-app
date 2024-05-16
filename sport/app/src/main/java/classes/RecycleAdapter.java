package classes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.example.sportactivity.R;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.RecycleViewHolder> {
    private ArrayList<Data> users;

    public RecycleAdapter(ArrayList<Data> users) {
        this.users = users;
    }

    /**
     * This method creates a new RecycleViewHolder by inflating the layout for each item in the RecyclerView.
     *
     * @param parent   The parent ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new RecycleViewHolder that holds the inflated view.
     */
    @NonNull
    @Override
    public RecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_recycleview,parent,false);
        return new RecycleViewHolder(v);
    }

    /**
     * This method binds the data to the RecycleViewHolder at the given position.
     *
     * @param holder   The RecycleViewHolder to bind data to.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecycleViewHolder holder, int position) {
        final Data currentUser = users.get(position);
        if (currentUser instanceof Guest) {
            int steps = 0;
            ArrayList<Session> sess = ((Guest) currentUser).getSes();
            if (sess != null) {
                if (sess.size()>0) {
                    for (int i = 0; i<sess.size() && sess.get(i) != null; i++) {
                        double distanceMeter = sess.get(i).getLength() * 1000;
                        double stepLenMeter = currentUser.getStep_size()/100;
                        steps = (int) (steps + (distanceMeter/stepLenMeter));
                    }
                }
            }
            holder.nameTextView.setText("name "+((Guest) currentUser).getUsername());
            holder.levelTextView.setText("level " +((Guest) currentUser).getLvl());
            holder.stepsTextView.setText("steps " +steps);
            holder.numberTextView.setText(""+(position+1));
            if (position == 0) holder.iconImageView.setImageResource(R.drawable.gold);
            else if (position == 1) holder.iconImageView.setImageResource(R.drawable.silver);
            else if (position == 2) holder.iconImageView.setImageResource(R.drawable.bronze);
            else holder.iconImageView.setImageResource(R.drawable.medal);
        }
        else if(currentUser instanceof User){
            int steps = 0;
            List<Session> sess = ((User) currentUser).getSes();
            if (sess != null) {
                for (int i = 0; sess.get(i) != null; i++) {
                    steps = (int) (steps + (sess.get(i).getLength()/currentUser.getStep_size()));
                }
            }
            holder.nameTextView.setText("name: " + ((Guest) currentUser).getUsername());
            holder.levelTextView.setText("level: "+((User) currentUser).getLvl());
            holder.stepsTextView.setText("steps: " + steps);
            holder.numberTextView.setText("" + position + 1);
            if (position == 0) holder.iconImageView.setImageResource(R.drawable.gold);
            else if (position == 1) holder.iconImageView.setImageResource(R.drawable.silver);
            else if (position == 2) holder.iconImageView.setImageResource(R.drawable.bronze);
            else holder.iconImageView.setImageResource(R.drawable.medal);
        }
    }

    /**
     * Returns the number of items in the data set held by the adapter.
     *
     * @return The number of items in the data set.
     */
    @Override
    public int getItemCount() {
        return users.size();
    }


    public static class RecycleViewHolder extends RecyclerView.ViewHolder{

        public TextView nameTextView;
        public TextView levelTextView;
        public TextView stepsTextView;
        public TextView numberTextView;
        public ImageView iconImageView;

        /**
         * This constructor initializes the views for each item in the RecyclerView.
         *
         * @param itemView The inflated view to be held by the RecyclerView.
         */
        public RecycleViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.rankerName);
            levelTextView = itemView.findViewById(R.id.lvl);
            stepsTextView = itemView.findViewById(R.id.reankerSteps);
            numberTextView = itemView.findViewById(R.id.rank);
            iconImageView = itemView.findViewById(R.id.imageView6);
        }
    }

}
