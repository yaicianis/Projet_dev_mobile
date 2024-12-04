package com.android.todo;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView userTextView;
    private FirebaseAuth auth;
    private RecyclerView rvTaskGroups;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);

        userTextView = view.findViewById(R.id.userText);
        rvTaskGroups = view.findViewById(R.id.rvTaskGroups);
        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String username = user.getDisplayName();
            if (username == null || username.isEmpty()) {
                username = user.getEmail();
            }
            userTextView.setText(username);
        } else {
            userTextView.setText("Guest");
        }

        rvTaskGroups.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvTaskGroups.setAdapter(new CategoryAdapter(getCategories(), requireContext()));

        return view;
    }

    private List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Work", R.drawable.ic_work));
        categories.add(new Category("Personal", R.drawable.ic_personal));
        categories.add(new Category("Daily", R.drawable.ic_daily));
        return categories;
    }
}
