package de.hawhamburg.budgettracker.ui.planer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import de.hawhamburg.budgettracker.databinding.FragmentPlanerBinding;

public class PlanerFragment extends Fragment {

    private FragmentPlanerBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PlanerViewModel planerViewModel =
                new ViewModelProvider(this).get(PlanerViewModel.class);

        binding = FragmentPlanerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textPlaner;
        planerViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}