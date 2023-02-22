package hu.zsof.tollapps.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import hu.zsof.tollapps.R
import hu.zsof.tollapps.RemoveButtonClickListener
import hu.zsof.tollapps.databinding.ItemMemberBinding
import hu.zsof.tollapps.network.repository.LocalDataStateService
import javax.inject.Inject

class MainAdapter @Inject constructor(
    private val listener: RemoveButtonClickListener,
    private val totalMembers: Int,
    private val price: Int,
) :
    RecyclerView.Adapter<MainAdapter.ListViewHolder>() {
    var memberList: List<String>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding: ItemMemberBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_member, parent, false)

        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        return holder.bind(memberList[position])
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount() = memberList.size

    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    inner class ListViewHolder(private val binding: ItemMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(name: String) {
            binding.name.text = name

            binding.removeApply.visibility = if (name.split(" ")[0] == LocalDataStateService.name) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }

            binding.removeApply.setOnClickListener {
                listener.onRemoveBtnClicked()
            }

            val multiplier = if (name.contains("1")) {
                2
            } else if (name.contains("2")) {
                3
            } else {
                1
            }
            if (price != 0) {
                val dividedPrice = price / totalMembers
                binding.price.text = "${(dividedPrice * multiplier)} Ft"
            }
        }
    }
}
