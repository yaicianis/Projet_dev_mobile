package com.android.todo;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment implements EnergyLevelAware  {

    private TextView userTextView;
    private FirebaseAuth auth;
    private RecyclerView rvTaskGroups;
    private RecyclerView rvDates;
    private String userEnergyLevel = "medium";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);

        userTextView = view.findViewById(R.id.userText);
        rvTaskGroups = view.findViewById(R.id.rvTaskGroups);
        rvDates = view.findViewById(R.id.rvDates);
        auth = FirebaseAuth.getInstance();

        fetchUserInfo();

        if (getActivity() instanceof MainActivity) {
            userEnergyLevel = ((MainActivity) getActivity()).getUserEnergyLevel();
            Log.d("HomeFragment", "User Energy Level: " + userEnergyLevel);
        } else {
            userEnergyLevel = "medium"; // Fallback to default if not retrieved
            Log.w("HomeFragment", "Failed to retrieve energy level. Using default: medium");
        }

        rvTaskGroups.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvTaskGroups.setAdapter(new CategoryAdapter(getCategories(), requireContext(), this.userEnergyLevel));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvDates.setLayoutManager(layoutManager);

        List<DateItem> dates = getLastWeekDates();
        DateAdapter adapter = new DateAdapter(dates, getContext());
        rvDates.setAdapter(adapter);

        rvDates.post(() -> {
            int centerPosition = (dates.size() - 1) / 2;
            layoutManager.scrollToPositionWithOffset(centerPosition, rvDates.getWidth() / 2 - getResources().getDimensionPixelSize(R.dimen.card_width) / 2);
        });

        return view;
    }

    private void fetchUserInfo() {
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(user.getUid());

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    String username = task.getResult().getString("username");
                    if (username == null || username.isEmpty()) {
                        username = user.getEmail(); // Fallback to email if username is not set
                    }
                    userTextView.setText(username);
                } else {
                    userTextView.setText("Guest");
                }
            });
        } else {
            userTextView.setText("Guest");
        }
    }

    private List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Work", R.drawable.ic_work));
        categories.add(new Category("Personal", R.drawable.ic_personal));
        categories.add(new Category("Daily", R.drawable.ic_daily));
        return categories;
    }

    private List<DateItem> getLastWeekDates() {
        List<DateItem> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = -3; i <= 3; i++) {
            calendar.add(Calendar.DAY_OF_YEAR, i);
            boolean isToday = i == 0;
            String month = new SimpleDateFormat("MMMM").format(calendar.getTime());
            String dayNumber = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            String dayName = days[calendar.get(Calendar.DAY_OF_WEEK) - 1];
            dates.add(new DateItem(month, dayNumber, dayName, isToday));
            calendar.add(Calendar.DAY_OF_YEAR, -i); // Reset
        }
        return dates;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("HomeFragment", "Resumed with User Energy Level: " + userEnergyLevel);
    }

    @Override
    public void onEnergyLevelChanged(String newEnergyLevel) {
        userEnergyLevel = newEnergyLevel;
        Log.d("HomeFragment", "Energy level updated to: " + userEnergyLevel);

        if (rvTaskGroups != null) {
            rvTaskGroups.setAdapter(new CategoryAdapter(getCategories(), requireContext(), userEnergyLevel));
        }
    }
}
