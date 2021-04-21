package edu.gwu.filmfinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


class RecyclerViewAdapter(val mData:List<Movie>): RecyclerView.Adapter<RecyclerViewAdapter.Companion.MyViewHolde>(){

    companion object {

        class MyViewHolde(itemView: View): RecyclerView.ViewHolder(itemView) {
            val movieTitle: TextView
            val movieImg: ImageView
            val cardView: CardView
            init {
                movieTitle = itemView.findViewById(R.id.movie_title) as TextView
                movieImg = itemView.findViewById(R.id.movie_img) as ImageView
                cardView = itemView.findViewById(R.id.cardView_movie) as CardView
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolde {
        var mInflater : LayoutInflater = LayoutInflater.from(parent.context)
        var view: View = mInflater.inflate(R.layout.card_view_movie_item,parent,false)
        return MyViewHolde(view)
    }


    override fun onBindViewHolder(holder: MyViewHolde, position: Int) {
        val currentMovies = mData[position]
        if (!Util.isStringEmpty(currentMovies.title)){
            holder.movieTitle.setText(mData.get(position).title)
            if (!Util.isStringEmpty(currentMovies.imageUrl)){
                Picasso
                    .get()
                    .load(mData.get(position).imageUrl)
                    .into(holder.movieImg)
            }

        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

}
