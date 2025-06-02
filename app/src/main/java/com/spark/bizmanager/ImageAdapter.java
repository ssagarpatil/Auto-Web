package com.spark.bizmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    // Updated interface to include success/failure callback
    public interface ImageDeleteListener {
        void onImageDeleted(String key, String storagePath, OnDeletionCompleteListener listener);

        // Callback interface for deletion result
        interface OnDeletionCompleteListener {
            void onSuccess();
            void onFailure(String error);
        }
    }

    private Context context;
    private List<ImageModel> imageList;
    private ImageDeleteListener deleteListener;

    public ImageAdapter(Context context, List<ImageModel> imageList, ImageDeleteListener deleteListener) {
        this.context = context;
        this.imageList = imageList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ImageModel model = imageList.get(position);

        Glide.with(context)
                .load(model.getImageUrl())
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop())
                .into(holder.imageView);

        holder.deleteButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete Image");
            builder.setMessage("Are you sure you want to delete this image?");
            builder.setPositiveButton("Delete", (dialog, which) -> {
                if (deleteListener != null) {
                    // Pass a callback to handle success/failure
                    deleteListener.onImageDeleted(model.getKey(), model.getStoragePath(), new ImageDeleteListener.OnDeletionCompleteListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(context, "Image deleted successfully", Toast.LENGTH_SHORT).show();
                            // Optionally remove the item from the list and notify the adapter
                            imageList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, imageList.size());
                        }

                        @Override
                        public void onFailure(String error) {
                            Toast.makeText(context, "Failed to delete image: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton deleteButton;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    public void updateList(List<ImageModel> newList) {
        imageList = newList;
        notifyDataSetChanged();
    }
}