package com.spark.bizmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {

    private final ArrayList<Product> productList;
    private final OnProductActionListener actionListener;

    public interface OnProductActionListener {
        void onDelete(Product product);
        void onEdit(Product product);
    }

    public ProductsAdapter(ArrayList<Product> productList, OnProductActionListener actionListener) {
        this.productList = productList;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.name.setText(product.getName());
        holder.price.setText("₹" + product.getPrice());

        Glide.with(holder.itemView.getContext())
                .load(product.getImageUrl())
                .centerCrop()
                .into(holder.image);

        holder.deleteIcon.setOnClickListener(v -> actionListener.onDelete(product));
        holder.editIcon.setOnClickListener(v -> actionListener.onEdit(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView image, deleteIcon, editIcon;
        TextView name, price;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageProduct);
            deleteIcon = itemView.findViewById(R.id.btnDelete);
            editIcon = itemView.findViewById(R.id.btnEdit);
            name = itemView.findViewById(R.id.textName);
            price = itemView.findViewById(R.id.textPrice);
        }
    }
}