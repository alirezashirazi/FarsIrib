package ir.farsirib.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ir.farsirib.Adapter.NationalTvRecyclerViewAdapter;
import ir.farsirib.Adapter.ProvincialTvRecyclerViewAdapter;
import ir.farsirib.Model.LiveItemObject;
import ir.farsirib.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NationalTvFragment extends Fragment {

    private GridLayoutManager lLayout;
    Context myContext;


    public NationalTvFragment( ) { myContext = this.getContext();
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View  rooView = inflater.inflate(R.layout.fragment_national_tv, container, false);

        myContext = this.getContext();

        List<LiveItemObject> rowListItem = getAllItemList();
        lLayout = new GridLayoutManager(myContext, 4);

        RecyclerView rView = rooView.findViewById(R.id.national_recycler_view);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);

        NationalTvRecyclerViewAdapter rcAdapter = new NationalTvRecyclerViewAdapter(rowListItem,myContext);
        rView.setAdapter(rcAdapter);

        return rooView;
    }

    private List<LiveItemObject> getAllItemList() {

        List<LiveItemObject> allItems = new ArrayList<LiveItemObject>();
        allItems.add(new LiveItemObject("شبکه یک", "http://www.mob.shahreraz.com/Farsirib/img/nationalTv/yek.png"));
        allItems.add(new LiveItemObject("شبکه دو", "http://www.mob.shahreraz.com/Farsirib/img/nationalTv/do.png"));
        allItems.add(new LiveItemObject("شبکه سه", "http://www.mob.shahreraz.com/Farsirib/img/nationalTv/se.png"));
        allItems.add(new LiveItemObject("شبکه چهار", "http://www.mob.shahreraz.com/Farsirib/img/nationalTv/chahar.png"));
        allItems.add(new LiveItemObject("شبکه پنج", "http://mob.shahreraz.com/Farsirib/img/nationalTv/tehran.png"));
        allItems.add(new LiveItemObject(" شبکه خبر", "http://mob.shahreraz.com/Farsirib/img/nationalTv/khabar.png"));
        allItems.add(new LiveItemObject("شبکه آموزش", "http://mob.shahreraz.com/Farsirib/img/nationalTv/amoozesh.png"));
        allItems.add(new LiveItemObject("شبکه قرآن و معارف", "http://mob.shahreraz.com/Farsirib/img/nationalTv/qoran.png"));
        allItems.add(new LiveItemObject("شبکه مستند", "http://mob.shahreraz.com/Farsirib/img/nationalTv/mostanad.png"));
        allItems.add(new LiveItemObject("شبکه آی فیلم", "http://mob.shahreraz.com/Farsirib/img/nationalTv/ifilmfarsi.png"));
        allItems.add(new LiveItemObject("شبکه نمایش", "http://mob.shahreraz.com/Farsirib/img/nationalTv/namayesh.png"));
        allItems.add(new LiveItemObject("شبکه تماشا", "http://mob.shahreraz.com/Farsirib/img/nationalTv/tamasha.png"));
        allItems.add(new LiveItemObject("شبکه ورزش", "http://mob.shahreraz.com/Farsirib/img/nationalTv/varzesh.png"));
        allItems.add(new LiveItemObject("شبکه نسیم", "http://mob.shahreraz.com/Farsirib/img/nationalTv/nasim.png"));
        allItems.add(new LiveItemObject("شبکه نهال", "http://mob.shahreraz.com/Farsirib/img/nationalTv/nahal.png"));
        allItems.add(new LiveItemObject("شبکه امید", "http://mob.shahreraz.com/Farsirib/img/nationalTv/omid.png"));
        allItems.add(new LiveItemObject("شبکه سلامت", "http://mob.shahreraz.com/Farsirib/img/nationalTv/salamat.png"));
        allItems.add(new LiveItemObject("شبکه شما", "http://mob.shahreraz.com/Farsirib/img/nationalTv/shoma.png"));
        allItems.add(new LiveItemObject("شبکه افق", "http://mob.shahreraz.com/Farsirib/img/nationalTv/ofogh.png"));
        allItems.add(new LiveItemObject("شبکه ایران کالا", "http://mob.shahreraz.com/Farsirib/img/nationalTv/irankala.png"));

        return allItems;
    }
}
