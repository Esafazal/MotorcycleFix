package com.fyp.motorcyclefix.RiderFragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.motorcyclefix.Dao.WorkshopDao;
import com.fyp.motorcyclefix.Patterns.WorkshopsAdapter;
import com.fyp.motorcyclefix.R;
import com.fyp.motorcyclefix.RiderFragments.WorkshopFragments.ViewWorkshopActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkshopFragment extends Fragment {

    private WorkshopsAdapter workshopsAdapter;
    private List<WorkshopDao> workshopDaoList;

    public WorkshopFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.rider_workshop_fragment, container, false);
        setHasOptionsMenu(true);

        fillExampleList();
        setUpRecyclerView(view);

        return view;
    }

    private void fillExampleList() {
        workshopDaoList = new ArrayList<>();
        workshopDaoList.add(new WorkshopDao(R.drawable.weather_wallpaper
                , "Sagara Mechanical Solutions", "Kawasaki | Hero-Honda"));
        workshopDaoList.add(new WorkshopDao(R.drawable.workshop_image
                , "MR Local Repairer", "TVS "));
        workshopDaoList.add(new WorkshopDao(R.drawable.rider_bike
                , "Raja motors", " Suzuki "));
        workshopDaoList.add(new WorkshopDao(R.drawable.weather_wallpaper
                , "Colombo motorists", "Hero-Honda | Ducati"));
        workshopDaoList.add(new WorkshopDao(R.drawable.workshop_image
                , "24/7 Unu Unu Repair", "Bajaj"));
        workshopDaoList.add(new WorkshopDao(R.drawable.rider_bike
                , "Sarath Engineering", "BMW | Yamaha"));
    }

    private void setUpRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.workshop_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        workshopsAdapter = new WorkshopsAdapter(workshopDaoList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(workshopsAdapter);
        workshopsAdapter.setOnItemClickListener(new WorkshopsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                String itemClick = String.valueOf(workshopDaoList.get(position));

                Intent intent = new Intent(getActivity(), ViewWorkshopActivity.class);
                intent.putExtra("workshopID", "1");
                intent.putExtra("workshop", itemClick);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_workshop, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                workshopsAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }
}
