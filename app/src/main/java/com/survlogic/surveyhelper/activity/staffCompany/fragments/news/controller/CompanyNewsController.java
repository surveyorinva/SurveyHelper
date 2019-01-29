package com.survlogic.surveyhelper.activity.staffCompany.fragments.news.controller;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.survlogic.surveyhelper.activity.staffCompany.fragments.news.adapter.CompanyNewsStaggeredAdapter;

import java.util.ArrayList;

public class CompanyNewsController {

    private static final String TAG = "CompanyNewsController";

    public interface CompanyNewsControllerListener{
        void refreshView();
    }

    private Context mContext;
    private Activity mActivity;

    private CompanyNewsControllerListener mListenerToActivity;

    private RecyclerView recyclerView;

    private static final int NUM_COLUMNS = 2;

    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mWebUrls = new ArrayList<>();

    public CompanyNewsController(Context context, CompanyNewsControllerListener listenerActivity) {
        this.mContext = context;
        this.mActivity = (Activity) context;

        this.mListenerToActivity = listenerActivity;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        initImageBitmaps();
    }

    private void initImageBitmaps(){
        mImageUrls.add("https://christopherconsultants.com/wp-content/uploads/2014/07/Andy-Gorecki1-e1441898938546-231x325.jpg");
        mNames.add("andy gorecki achieves dbia certification");
        mWebUrls.add("https://christopherconsultants.com/andy-gorecki-dbia/");

        mImageUrls.add("https://christopherconsultants.com/wp-content/uploads/2019/01/Joe-Miller-cropped-337x325.jpg");
        mNames.add("joe miller earns professional engineer (pe) licensure");
        mWebUrls.add("https://christopherconsultants.com/joe-miller-pe/");

        mImageUrls.add("https://christopherconsultants.com/wp-content/uploads/2018/11/Katie-McDaniel-2018_resized_cropped-344x500.jpg");
        mNames.add("katie mcdaniel earns deq swm certifications");
        mWebUrls.add("https://christopherconsultants.com/katie-swmdeq/");

        mImageUrls.add("https://christopherconsultants.com/wp-content/uploads/2018/09/katherine-andy_christopher-consultants-virginia-tech-e1538056061652-768x480.jpg");
        mNames.add("find us at career fairs this fall!");
        mWebUrls.add("https://christopherconsultants.com/find-us-at-career-fairs-this-fall/");

        mImageUrls.add("https://christopherconsultants.com/wp-content/uploads/2018/07/8th-graders-May-2018-cropped-285x325.png");
        mNames.add("mentoring the next generation");
        mWebUrls.add("https://christopherconsultants.com/mentoring/");

        mImageUrls.add("https://media.licdn.com/media-proxy/ext?w=2801&h=1505&f=pj&hash=G%2FWGvj9MAx7tKUiVMcFTYORgE7U%3D&ora=1%2CaFBCTXdkRmpGL2lvQUFBPQ%2CxAVta5g-0R6nlh8Tw1It6a2FowGz60oIQY_PTWn8CnL_5aaFZnXrCKaqDemhpQNUHilD7lMtKbr9FHO5Q9HpNsm3P4Uv0PjtJZnmKVdZbEFn0D0YtINob0l084L7B-7kYWhaj9RpIBmXKqjNQQIFBCMTyOGxI9LLHGk86GWtQt_EOp5rS4Jv2LZR9BNkv84");
        mNames.add("Reflections on 2018");
        mWebUrls.add("https://www.bizjournals.com/washington/news/2018/12/28/2018-reflections-d-c-area-executives-open-up-about.html#g/447656/13");

        mImageUrls.add("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJMAAACTCAMAAAC9O9snAAAAYFBMVEU6VZ////8yUJzN0eIoSJmgqss3U55RZ6jq7PNhc65CW6Le4ez09fnt8PY8V6DV2egtTJuCkL0RPJV9i7qXosYfQ5dKYaWostBWa6qwuNS7w9rCyN0WP5ZneLBygrWJlcAs3KrgAAAB80lEQVR4nO3a0XKDIBAFUKOGGBQlVpOYxPb//7Jp+1SRiLPuajv3PvNwZnGARaIIQRAEQRAEQZB/HaVUPJbVQHFb2/P1OJZ1QNbqvsuzzBQjyUolDlKp/bgXO2+MvMlGTeYHfZuERepxei2Sr5M9JxMicVPaTxVJfO7S07RIuE5hJNE62fcgkmSd4uuLNWmlOlkTRhKsU3sJJMmZ4sCPSXDuyrqaYZKpk+2DSWJ1stNbinSd1DFwHRCsU9qEk4TqVNb5LJMAKVJ2BmlXiJgmdrqs+pVEZO7S/QtQowcdlczq1N68pFuUqmFETP7VqXvI900/iX2mXGii5ph6uxbJazL1aiSvqWu3Z9qnMMEEE0wwwQQTTNIm58yvfOfxvdsfMLUI58Mwvkvxi3aGfo1eXqUTkw3jucEonIHPmGb5Q7qeczcwli2a3rZnKo7L/+gkmzTDN040GYYGi2rKGRpRqun22J6pYdhwqCaGpYBsujJseFQTw3ZHNWV6eRLVVHHcRBNNd46rO6Kp26CJY3mimk4brBPDSYVq4jipUE2GYQemmjhOKk9TwNMdfxKWK/Ny5OGe50d+5w498PyAcR83evvg1hkq93xm9fsCmGCCCSaYYIIJJphgggkmmGCCCSaYYIIJJphgggkmmGCCCSaYYPrrpk+Oaijbsfn2NwAAAABJRU5ErkJggg==");
        mNames.add("Facebook Posts");
        mWebUrls.add("https://www.facebook.com/");

        mImageUrls.add("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAOEAAADhCAMAAAAJbSJIAAAAhFBMVEUAe7X///8AcbDi6/MAd7N9rM4Ac7G70eStyN/I2ukAebQAdbIAcLDY6fLM4e5QmsXn8ve10+U5j7/0+vx2qc2Cs9Ntq88Af7idw9uly+EgiLxvpMpTnsg4k8JEk8Lq8/i92uqev9qNu9jR4+6uy+CZvNh4sNGvyuCIstJinceTuNUxir3eoYF2AAAEwklEQVR4nO2dbXfiKhRGARk6gjEvjho1tzq215lO////u1FrrTZQOwsP9dxnf+kHk7WyCxxIOICQB7K6mU8FC+ZNnb16iZe/ZaWcTv1k0dBOVeWJYZErm/qpImNVXhwNh4JP8R3RYngwHA64FeAeOxjuDQvBU7BVFMXOMOdYRffofGtYqtTPcUVU2RpWfIuwraeVFBnnImwLMRO1S/0QV8XVouFcSdtY04h56me4MnPBZLANAAAAAAAAAAAAAAAAAID/NVYbpQbKOKZpANboRT3sFVm5rgYM5yCtmc5e86pkb2K4laN1a3lCdm9SP1NU7D7b6IQNp3wAa7N3glKO+Sha974EtyzYZASYdaegHM25hJtpt6CUJZNoY2Y+Q8mjEK0deQ1rFoWon7yCsmARTl3tN5Qs0o/UKmCYc2iIqqu3P/CDQ5eoegHDbxwMzUPAkMWwxvm7QykrDu3Qjf2CI83B0FZ+wxWLHj8UTCccmmFoUFPwKMJAIbKIpFu0pyUyaYVb3KRLMOMQRw+YTYfglJOhMPnyTLDktgDOTU+GNsWCTxt8xYh1tn/bH62aAZcoeopT03zx46nSiqffDq21tcwaIADgK2CUn9Og6gJXns0aW9sGLWdecNsARqh0ivnV9zJ8eqvo/viv7P8+KlrrjHjOJ+P68a795a6crSf5szWp9um4/FubuQtc+eswCGpNmvVDcT5VMCr66/s0+QGRDL/vDbWqHv3zIEU9TZAgENPQmqp7svVI+Uw+4o1pqENzIAdq6o1XIhrqjwpwT1HRNsdohtaEZkBOmJDW1GiGpgz8fMaaUjGW4aC5XFDKMaFiNMOL6+iOhq4txjLMPyVImQURybD+RCvc8UCW5BnL0D+S8bChqqeRDC/rCt8yonrdiGT4F1Bl66QzpJp/TWcoNzQD1ISGGU01TWgo70kKMaUhTaxJaZiRdIkpDeUzRTRNakiS7JHUsKSopkkNSfqLpIaSYliT1vAfgoaY1nBM0BCjGy5Xs/pnPVsVl1w8I2iIcQ2z8Xy7RtM5YwbTzccvjSuC1QAxDZcL8zZ0OJV/9HmqGNyU4fuN0rX+6PPNTdXSzo3SB4/hmwi2W41muOxMhLMffEclyCSPZjjpDvyhLOuW5nYMe74WZUKLcijWrMQyXPv6bh2c0CDo8mMZ+r/ST88zOy/6x3w5w5G/YwuuyiHYYD3WN29/xxZYpNoO227GMLBm+Hw3g0vv+2KGjwHDb4H7CBYExM4Yeo8OGl5/6B03Y+jThkP2hg83U0v/1pDgwJHEhj0YwhCGMIQhDGEIQxjCEIYwhCEMYQhDGMIQhjCEIQxhCEMYwhCGMIQhDGEIQxjCEIYwhCEMYQhDGMIQhjBMYhhYb5Ha0PR7XpaLt8u13Hf/lb1//avs7GLpv69PsEw2tI/w6Xq00D7CoWWENnAfw/37AQAAAAAAAAAAAAAAAAAAAAAAgCPz1A9wZeaiYXz0u9juSiwItq1NiasFwVaLKVGZoNjlPB26kkKWnAvRlK2hzPnGGp3LrWEhuNZTK4qdYfcxDAywg+2hJ1vDjqM0OKDF7lSXnaEscsWtGLXJ92fziJfM1LJSjk9Baqeqw3ku4jX7NqsbLmPUeVMfTzr5D3pkWtnuvn/IAAAAAElFTkSuQmCC");
        mNames.add("LinkedIn Posts");
        mWebUrls.add("https://www.linkedin.com/company/christopher-consultants-ltd-/");

        initRecyclerView();

    }

    private void initRecyclerView(){
        CompanyNewsStaggeredAdapter staggeredRecyclerViewAdapter =
                new CompanyNewsStaggeredAdapter(mContext, mNames, mImageUrls, mWebUrls);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(staggeredRecyclerViewAdapter);
    }


}
