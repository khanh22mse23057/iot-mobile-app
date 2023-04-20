package code.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.material.components.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterImage extends RecyclerView.Adapter<AdapterImage.ImageViewHolder>{

    private List<Bitmap> listBitmap = new ArrayList<>();

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.img.setImageBitmap(listBitmap.get(position));
    }

    @Override
    public int getItemCount() {
        return listBitmap.size();
    }

    public void addImage(String message) {
        try {
            byte[] decodedString = Base64.decode(message, Base64.DEFAULT);
            // Convert the byte array to a Bitmap
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            listBitmap.add(0,decodedBitmap);

            this.notifyItemInserted(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id._id_image);
        }
    }
}
