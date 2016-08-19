package com.booster.avivast.antivirus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

import at.grabner.circleprogress.CircleProgressView;
import com.booster.avivast.R;

/**
 * Created by hexdump on 02/11/15.
 */

public class MainFragment extends Fragment
{
    final String _logTag=this.getClass().getSimpleName();
    final Random _random=new Random();

    AntivirusActivity getMainActivity() {return (AntivirusActivity) getActivity();}
    //AppData getAppData() { return getMainActivity().getAppData();}

    Button _runAntivirusNow=null;
    Button _resolvePersistProblems = null;

    //Scrollable data chunk data
    ImageView _progressPanelIconImageView;
    TextView _progressPanelTextView;
    CircleProgressView _circleProgressBar;
    TextView _bottomMenacesCounterText;
    TextView _bottomScannedAppsText;
    RelativeLayout _progressContainer;
    RelativeLayout _buttonContainer;
    RelativeLayout _superContainer;
    RelativeLayout _noMenacesInformationContainer;
    ImageView _riskIcon;
    LinearLayout _deviceRiskPanel;
    LinearLayout _scanningProgressPanel;
    TextView _topMenacesCounterText;
    TextView _lastScanText;


    private boolean firstScan = false;
    final int kProgressBarRefressTime=50;

    final int kEnterAppTime=50;
    final int kScanningAppTime=100;
    final int kIconChangeToGoodOrBadTime =100;

    ScanningFileSystemAsyncTask _currentScanTask=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);

        _setupFragment(rootView);

        return rootView;
    }

    protected void _setupFragment(View root)
    {

        _progressPanelIconImageView =(ImageView)root.findViewById(R.id.animationProgressPanelIconImageView);
        _progressPanelTextView =(TextView)root.findViewById(R.id.animationProgressPanelTextView);
        _circleProgressBar=(CircleProgressView) root.findViewById(R.id.circleView);
        _bottomMenacesCounterText=(TextView) root.findViewById(R.id.bottomFoundMenacesCount);
        _bottomScannedAppsText=(TextView) root.findViewById(R.id.bottomScannedApp);
        _buttonContainer=(RelativeLayout)root.findViewById(R.id.buttonLayout);
        _progressContainer=(RelativeLayout)root.findViewById(R.id.animationProgressPanel);
        _superContainer=(RelativeLayout)root.findViewById(R.id.superContainer);
        _noMenacesInformationContainer=(RelativeLayout) root.findViewById(R.id.noMenacesFoundPanel);
        _riskIcon = (ImageView) root.findViewById(R.id.iconRisk);
        _deviceRiskPanel = (LinearLayout) root.findViewById(R.id.deviceRiskPanel);
        _scanningProgressPanel=(LinearLayout) root.findViewById(R.id.scanningProgressPanel);
        _resolvePersistProblems = (Button) root.findViewById(R.id.button_resolve_problems);
        _resolvePersistProblems.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
              if(getMainActivity().canShowAd())
              {
                  AntivirusActivity ma=getMainActivity();
                  ma.getAppData().setLastAdDate(new DateTime());
                  ma.getAppData().serialize(ma);

                  _requestDialogForAd(null,true);

              }
              else
              {

                  _doActionResolveProblemsButton();
              }



            }
        });


        _topMenacesCounterText = (TextView)root.findViewById(R.id.topMenacesCounter);
        _runAntivirusNow=(Button)root.findViewById(R.id.runAntivirusNow);
        _runAntivirusNow.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                _runAntivirusNow.setEnabled(false);

                if(!StaticTools.isNetworkAvailable(getMainActivity()))
                {
                    getMainActivity().showNoInetDialog();
                    return;
                }

//                getMainActivity().getMenu().setGroupVisible(0,false);
                _scanFileSystem();

            }
        });

        _lastScanText=(TextView) root.findViewById(R.id.lastScanText);
        DateTime time=getMainActivity().getAppData().getLastScanDate();
        if(!time.equals(AppData.kNullDate))
        {
            DateTimeFormatter dtf= DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");
            String str=StaticTools.fillParams(getString(R.string.last_scanned),"#",dtf.print(time));
            _lastScanText.setText(str);
        }
        else
        {
            String str=StaticTools.fillParams(getString(R.string.last_scanned),"#", getString(R.string.never));
            _lastScanText.setText(str);
        }



        setUIRiskState();

        _resetFormLayout();
    }

    private void _resetFormLayout()
    {
        _progressContainer.setVisibility(View.INVISIBLE);

        _buttonContainer.setVisibility(View.VISIBLE);
        _buttonContainer.setTranslationX(0);

        _runAntivirusNow.setEnabled(true);
    }

    protected void _startRealScan()
    {

        getMainActivity().startMonitorScan(new MonitorShieldService.IClientInterface()
        {
            @Override
            public void onMonitorFoundMenace(IProblem menace)
            {
            }

            @Override
            public void onScanResult(List<PackageInfo> allPacakgesToScan, Set<IProblem> scanResult)
            {
                AppData appData = getMainActivity().getAppData();
                appData.setFirstScanDone(true);
                appData.serialize(getMainActivity());

                _startScanningAnimation(allPacakgesToScan, scanResult);
            }
        });
    }

    private void _scanFileSystem()
    {
        _scanningProgressPanel.setAlpha(0.0f);
        _scanningProgressPanel.setVisibility(View.VISIBLE);
        _circleProgressBar.setValue(0);

        ObjectAnimator oa1 = ObjectAnimator.ofFloat(_scanningProgressPanel, "alpha",0.0f,1.0f);
        oa1.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                _startRealScan();
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {
            }
        });
        oa1.setDuration(500);
        oa1.start();

        oa1 = ObjectAnimator.ofFloat(_deviceRiskPanel, "alpha",1.0f,0.0f);
        oa1.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                _deviceRiskPanel.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {
            }
        });

        oa1.setDuration(500);
        oa1.start();
    }

    private void _topProgressBarGoesToScanningState(int transitionTime)
    {
        _deviceRiskPanel.setAlpha(0.0f);
        _deviceRiskPanel.setVisibility(View.VISIBLE);

        ObjectAnimator oa1 = ObjectAnimator.ofFloat(_deviceRiskPanel, "alpha",0.0f,1.0f);
        oa1.setDuration(transitionTime);
        oa1.start();

        oa1 = ObjectAnimator.ofFloat(_scanningProgressPanel, "alpha",1.0f,0.0f);
        oa1.setDuration(transitionTime);
        oa1.start();
    }

    private void _startScanningAnimation(final List<PackageInfo> allPackages, final Collection<? extends IProblem> tempBadResults)
    {
        //Animate the button exit
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(_buttonContainer, "translationX",
                0,
                -_superContainer.getWidth()/2.0f-_buttonContainer.getWidth());
        oa1.setDuration(100);
        oa1.setInterpolator(new LinearInterpolator());
        oa1.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                _configureScanningUI();

                Collection<IProblem> appProblems=new ArrayList<IProblem>();
                ProblemsDataSetTools.getAppProblems(tempBadResults,appProblems);

                _currentScanTask = new ScanningFileSystemAsyncTask(getMainActivity(), allPackages, appProblems);
                _currentScanTask.setAsyncTaskCallback(new IOnActionFinished()
                {
                    @Override
                    public void onFinished()
                    {
                        _currentScanTask = null;

                        AppData appData = getMainActivity().getAppData();
                        appData.setLastScanDate(new DateTime());
                        appData.serialize(getContext());

                        if (getMainActivity().canShowAd())
                        {
                            _requestDialogForAd(tempBadResults, false);

                        } else
                        {

                            _doAfterScanWork(tempBadResults);
                        }


                    }
                });

                _currentScanTask.execute();
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {
            }
        });
        oa1.start();
    }

    void _doAfterScanWork(final Collection<? extends IProblem> tempBadResults)
    {

        _currentScanTask = null;
        if (getMainActivity().getMenacesCacheSet().getItemCount() > 0)
        {
            showResultFragment(new ArrayList<IProblem>(tempBadResults));

            Handler handler = new Handler();
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    _configureNonScanningUI();
                }
            }, 400);
        } else
        {
            _playNoMenacesAnimationFound();
        }


    }

    void _doActionResolveProblemsButton()
    {

        Set<IProblem> foundMenaces=getMainActivity().getProblemsFromMenaceSet();

        if (foundMenaces !=null && foundMenaces.size() !=0)
        {

            showResultFragment(new ArrayList<IProblem>(foundMenaces));

        }


    }

    void _showInterstitial(final Collection<? extends IProblem> tempBadResults, final boolean isResolveButtonPressed)
    {
            getMainActivity().setInterstitialListener(new AdListener()
            {
                @Override
                public void onAdClosed()
                {
                    super.onAdClosed();

                    if (!isResolveButtonPressed)
                        _doAfterScanWork(tempBadResults);
                    else
                        _doActionResolveProblemsButton();
                }

                @Override
                public void onAdFailedToLoad(int errorCode)
                {
                    super.onAdFailedToLoad(errorCode);
                    if (!isResolveButtonPressed)
                        _doAfterScanWork(tempBadResults);
                    else
                        _doActionResolveProblemsButton();
                }

                @Override
                public void onAdLeftApplication()
                {
                    super.onAdLeftApplication();
                }

                @Override
                public void onAdOpened()
                {
                    super.onAdOpened();
                }

                @Override
                public void onAdLoaded()
                {
                    super.onAdLoaded();
                }
            });

            getMainActivity().showInterstitial();


    }

    void _requestDialogForAd(final Collection<? extends IProblem> tempBadResults, final boolean isResolveProblem)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        AlertDialog alertDialog= builder
                .setTitle(this.getString(R.string.warning))
                .setMessage(this.getString(R.string.install_application))
                .setPositiveButton(this.getString(R.string.accept_eula), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        _showInterstitial(tempBadResults, isResolveProblem);
                    }
                })
                .setNegativeButton(this.getString(R.string.cancel), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (!isResolveProblem)
                            _doAfterScanWork(tempBadResults);
                        else
                            _doActionResolveProblemsButton();
                    }
                }).create();

        _showTimedDialog(alertDialog,true,false,true);

    }

    protected void _showTimedDialog(AlertDialog dialog, boolean negative, boolean blockPositive, boolean blockNegative)
    {
        dialog.show();

        Handler handler = new Handler();

        // Access the button and set it to invisible
        final Button posButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if(blockPositive)
            posButton.setEnabled(false);

        final Button negButton=dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        final boolean finalNegative=negative;

        if(negative && blockNegative)
        {
            negButton.setEnabled(false);
        }

        // Post the task to set it visible in 5000ms
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (posButton != null)
                    posButton.setEnabled(true);
                if (finalNegative)
                    negButton.setEnabled(true);
            }
        }, 20000);
    }

    void _playNoMenacesAnimationFound()
    {
        ObjectAnimator oa = ObjectAnimator.ofFloat(_progressContainer, "rotationY", 0f, 90.0f);

        oa.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);

                _noMenacesInformationContainer.setVisibility(View.VISIBLE);
                _progressContainer.setVisibility(View.INVISIBLE);
                _progressContainer.setRotationY(0);
                ObjectAnimator oa = ObjectAnimator.ofFloat(_noMenacesInformationContainer, "rotationY", -90f, 0.0f);
                oa.addListener(new AnimatorListenerAdapter()
                {
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                ObjectAnimator oa = ObjectAnimator.ofFloat(_noMenacesInformationContainer, "rotationY", 0, 90.0f);
                                oa.addListener(new AnimatorListenerAdapter()
                                {
                                    @Override
                                    public void onAnimationEnd(Animator animation)
                                    {
                                        _noMenacesInformationContainer.setRotationY(0);
                                        _noMenacesInformationContainer.setVisibility(View.INVISIBLE);
                                        _buttonContainer.setVisibility(View.VISIBLE);
                                        _buttonContainer.setTranslationX(0);

                                        setUIRiskState();

                                        _topProgressBarGoesToScanningState(200);

                                        ObjectAnimator oa = ObjectAnimator.ofFloat(_buttonContainer, "rotationY", -90f, 0.0f);
                                        oa.addListener(new AnimatorListenerAdapter()
                                        {
                                            @Override
                                            public void onAnimationEnd(Animator animation)
                                            {
                                                getMainActivity().getMenu().setGroupVisible(0,true);
                                                _runAntivirusNow.setEnabled(true);
                                            }
                                        });
                                        oa.setDuration(100);
                                        oa.start();
                                    }
                                });
                                oa.setDuration(100);
                                oa.setInterpolator(new LinearInterpolator());
                                oa.start();
                            }
                        },2000);
                    }
                });

                oa.setDuration(100);
                oa.setInterpolator(new LinearInterpolator());
                oa.start();
            }
        });

        oa.setDuration(100);
        oa.setInterpolator(new LinearInterpolator());
        oa.start();
    }
    void showResultFragment(List<IProblem> problems)
    {
        ResultsFragment newFragment= (ResultsFragment) getMainActivity().slideInFragment(AntivirusActivity.kResultFragmentTag);
        newFragment.setData(getMainActivity(), problems);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(_currentScanTask!=null)
            _currentScanTask.pause();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if(getMainActivity().getAppData().getFirstScanDone())
            getMainActivity().updateMenacesAndWhiteUserList();

        if(_currentScanTask!=null)
            _currentScanTask.resume();
        else
            setUIRiskState();


    }

    void setUIRiskState()
    {
        boolean firstScanDone =getMainActivity().getAppData().getFirstScanDone();
        Set<IProblem> foundMenaces = getMainActivity().getProblemsFromMenaceSet();
        boolean isDangerous = _isDangerousAppInSet(foundMenaces);

        if(foundMenaces.isEmpty() || foundMenaces==null)
        {
            if(!firstScanDone)
            {
                _riskIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.shield_medium_risk_icon));
                _topMenacesCounterText.setText(R.string.execute_first_analysis);
                _deviceRiskPanel.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.MediumRiskColor));
                _resolvePersistProblems.setVisibility(View.GONE);
            }
            else
                activateProtectedState();

        }
        else
        {
            if(isDangerous)
                activateHighRiskState(foundMenaces.size());
            else
                activateMediumRiskState(foundMenaces.size());


        }
    }

    private boolean _isDangerousAppInSet(Set<IProblem> set)
    {
        for(IProblem bprd : set)
        {
            if(bprd.isDangerous())
                return true;
        }

        return false;
    }

    void activateProtectedState()
    {
        _configureNonScanningUI();

        _riskIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.shield_protected_icon));
        _deviceRiskPanel.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.ProtectedRiskColor));
        _topMenacesCounterText.setText(R.string.are_protected);
       _resolvePersistProblems.setVisibility(View.GONE);


    }

    void activateMediumRiskState(int menaces)
    {
        _configureNonScanningUI();

        _riskIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.shield_medium_risk_icon));
        _deviceRiskPanel.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.MediumRiskColor));
        _updateFoundThreatsText(_topMenacesCounterText, menaces);
        _resolvePersistProblems.setVisibility(View.VISIBLE);
        StaticTools.setViewBackgroundDrawable(_resolvePersistProblems, ContextCompat.getDrawable(getContext(), R.drawable.resolve_button_medium_risk));

    }

    void activateHighRiskState(int menaces)
    {
        _configureNonScanningUI();

        _riskIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.shield_high_risk_icon));
        _deviceRiskPanel.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.HighRiskColor));
        _updateFoundThreatsText(_topMenacesCounterText, menaces);
        _resolvePersistProblems.setVisibility(View.VISIBLE);
        StaticTools.setViewBackgroundDrawable(_resolvePersistProblems, ContextCompat.getDrawable(getContext(), R.drawable.resolve_button_selector));
    }


    void _updateFoundThreatsText(TextView textView, int appCount)
    {
        String finalStr=getString(R.string.menaces_unresolved);
        finalStr= StaticTools.fillParams(finalStr, "#", Integer.toString(appCount));
        textView.setText(finalStr);
    }

    void _configureScanningUI()
    {
        Menu menu=getMainActivity().getMenu();

        if(menu!=null)
            menu.setGroupVisible(0,false);

        _scanningProgressPanel.setAlpha(1.0f);
        
        _progressContainer.setVisibility(View.VISIBLE);
        _progressContainer.setTranslationX(0);
        _buttonContainer.setVerticalGravity(View.INVISIBLE);
    }

    void _configureNonScanningUI()
    {
        if(getMainActivity()!=null && getMainActivity().getMenu()!=null)
        {
            Menu menu=getMainActivity().getMenu();
            menu.setGroupVisible(0,true);
        }

        _progressContainer.setVisibility(View.INVISIBLE);
        _progressContainer.setTranslationX(0);
        _buttonContainer.setVerticalGravity(View.VISIBLE);
        _buttonContainer.setTranslationX(0);
    }


}
