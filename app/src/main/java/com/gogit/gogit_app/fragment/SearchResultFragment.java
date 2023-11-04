package com.gogit.gogit_app.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gogit.gogit_app.R;
import com.gogit.gogit_app.adapter.SearchUserAdapter;
import com.gogit.gogit_app.client.GithubRetrofitClient;
import com.gogit.gogit_app.config.SessionManager;
import com.gogit.gogit_app.model.SearchedUser;
import com.gogit.gogit_app.service.GithubService;
import com.gogit.gogit_app.util.MyToast;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultFragment extends Fragment {

    private static final String KEYWORD = "KEYWORD";

    private String keyword;
    RecyclerView userSearchView;
    RecyclerView repoSearchView;

    public SearchResultFragment() {
    }

    public static SearchResultFragment newInstance(String keyword) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putString(KEYWORD, keyword);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            keyword = getArguments().getString(KEYWORD);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_search, container, false);

        SessionManager sessionManager = new SessionManager(getContext());
        String token = sessionManager.getToken();

        userSearchView = view.findViewById(R.id.user_search_recyclerview);
        userSearchView.setHasFixedSize(false);
        userSearchView.setLayoutManager(new LinearLayoutManager(getContext()));

        repoSearchView = view.findViewById(R.id.repo_search_recyclerview);
        repoSearchView.setHasFixedSize(false);
        repoSearchView.setLayoutManager(new LinearLayoutManager(getContext()));

        setUserSearchResult(token, keyword);

        Log.d("my tag", keyword);
        return view;
    }

    private void setUserSearchResult(String token, String keyword) {
        Retrofit retrofit = GithubRetrofitClient.getRetrofitInstance();
        GithubService githubService = retrofit.create(GithubService.class);
        Call<SearchedUser> call = githubService.getUserSearchResult(
                "Bearer " + token,
                keyword,
                "followers",
                5
        );

        call.enqueue(new Callback<SearchedUser>() {
            @Override
            public void onResponse(Call<SearchedUser> call, Response<SearchedUser> response) {
                SearchedUser searchedUser = response.body();
                userSearchView.setAdapter(new SearchUserAdapter(searchedUser.getItems()));
                Log.d("my tag", searchedUser.getItems().toString());
            }

            @Override
            public void onFailure(Call<SearchedUser> call, Throwable t) {
                MyToast.showNetworkErrorToast(getContext());
            }
        });
    }
}