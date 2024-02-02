package com.example.inventorycountingapp.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.inventorycountingapp.R
import com.example.inventorycountingapp.common.load
import com.example.inventorycountingapp.databinding.ListItemProductBinding

class SelectedProductAdapter : RecyclerView.Adapter<SelectedProductAdapter.MyViewHolder>() {

    private val items: MutableList<ProductResponse.Data> = mutableListOf()
    var onItemClick: ((ProductResponse.Data) -> Unit) ?= null

    fun setData(newItems: MutableList<ProductResponse.Data>) {
        val diffCallback = MyDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemProductBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        
        holder.itemView.setOnClickListener{
            onItemClick?.invoke(items[position])
        }
        
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class MyViewHolder(private val binding: ListItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProductResponse.Data) {
            binding.apply {
                ivProductImage.load(item.imagePath, R.drawable.ic_default_item)
                tvName.text = item.name
                tvQuantity.text = item.defaultQty
                tvPricee.text = item.salePriceTax
                textDocsFilesSize.text = item.unpackSize
            }
        }
    }

    inner class MyDiffCallback(
        private val oldList: List<ProductResponse.Data>,
        private val newList: List<ProductResponse.Data>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].defaultQty == newList[newItemPosition].defaultQty
        }
    }

}
