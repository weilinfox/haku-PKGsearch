package me.weilinfox.pkgsearch.ui.favourite;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.w3c.dom.Text;

import me.weilinfox.pkgsearch.R;
import me.weilinfox.pkgsearch.databinding.FragmentFavouriteBinding;
import me.weilinfox.pkgsearch.utils.NavigationViewUtil;

public class FavouriteFragment extends Fragment {

    private static final String TAG = "FavouriteFragment";
    private FavouriteViewModel favouriteViewModel;
    private FragmentFavouriteBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        favouriteViewModel =
                new ViewModelProvider(this).get(FavouriteViewModel.class);

        binding = FragmentFavouriteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView favHold = binding.favHold;
        favHold.setHeight(NavigationViewUtil.height);
        
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}