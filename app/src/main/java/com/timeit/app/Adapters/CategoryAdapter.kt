package com.timeit.app.Adapters

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.timeit.Database.TasksDAO
import com.timeit.app.DataModels.Category
import com.timeit.app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryAdapter(var categoryList: MutableList<Category>, private var tasksDAO: TasksDAO, var context: Context): RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.categoryName.text = category.categoryName

        // Highlight the selected category
        if (position == selectedPosition) {
            holder.categoryName.setBackgroundColor(Color.TRANSPARENT)
            holder.block.setBackgroundResource(R.drawable.selected_day_bg)
            holder.categoryName.setTextColor(Color.WHITE)
        } else {
            holder.block.setBackgroundColor(Color.TRANSPARENT)
            holder.categoryName.setBackgroundResource(R.drawable.category_bg)
            holder.categoryName.setTextColor(Color.BLACK)
        }

        holder.itemView.setOnClickListener {
            // Update the selected position
            val previousPosition = selectedPosition
            selectedPosition = position

            // Notify the adapter to update the UI
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
        }

        holder.itemView.setOnLongClickListener {
            showDeleteDialog(holder.itemView, position)
            true
        }

    }

    private fun showDeleteDialog(view: View, position: Int) {
        val builder = AlertDialog.Builder(view.context)
        builder.setTitle("Delete Category")
        builder.setMessage("Are you sure you want to delete this category?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            // Remove the item from the list and notify the adapter
            if (categoryList[position].categoryName == "General") {
                Toast.makeText(context, "Cannot delete General category", Toast.LENGTH_SHORT).show()
            } else {
                // Remove the item from the list and notify the adapter
                CoroutineScope(Dispatchers.Main).launch {
                    tasksDAO.deleteCategory(categoryList[position].categoryId)
                    categoryList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, categoryList.size)
                }
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    fun updateAdapter(categoryList: MutableList<Category>){
        this.categoryList = categoryList
        notifyDataSetChanged()
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.category_name)
        val block: LinearLayout = itemView as LinearLayout
    }
}