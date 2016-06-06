package me.yokeyword.sample.multiple;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.SupportFragment;
import me.yokeyword.fragmentation.anim.FragmentAnimator;
import me.yokeyword.sample.R;
import me.yokeyword.sample.multiple.event.TabSelectedEvent;
import me.yokeyword.sample.multiple.ui.fragment.BaseLazyMainFragment;
import me.yokeyword.sample.multiple.ui.fragment.first.FirstHomeFragment;
import me.yokeyword.sample.multiple.ui.fragment.first.MultiFirstFragment;
import me.yokeyword.sample.multiple.ui.fragment.fourth.MultiFourthFragment;
import me.yokeyword.sample.multiple.ui.fragment.second.MultiSecondFragment;
import me.yokeyword.sample.multiple.ui.fragment.second.ViewPagerFragment;
import me.yokeyword.sample.multiple.ui.fragment.third.MultiThirdFragment;
import me.yokeyword.sample.multiple.ui.view.BottomBar;
import me.yokeyword.sample.multiple.ui.view.BottomBarTab;

/**
 * 类知乎 复杂嵌套Demo
 * Created by YoKeyword on 16/6/2.
 */
public class MainActivity extends SupportActivity implements BaseLazyMainFragment.OnBackToFirstListener {
    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;
    public static final int FOURTH = 3;

    private SupportFragment[] mFragments = new SupportFragment[4];

    private BottomBar mBottomBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multi_activity_main);
//        EventBus.getDefault().register(this);

        if (savedInstanceState == null) {
            mFragments[FIRST] = MultiFirstFragment.newInstance();
            mFragments[SECOND] = MultiSecondFragment.newInstance();
            mFragments[THIRD] = MultiThirdFragment.newInstance();
            mFragments[FOURTH] = MultiFourthFragment.newInstance();

            loadMultipleRootFragment(R.id.fl_container, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
                    mFragments[THIRD],
                    mFragments[FOURTH]);
        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题

            // 这里我们需要拿到mFragments的引用,也可以通过getSupportFragmentManager.getFragments()自行进行判断查找(效率更高些),用下面的方法查找更方便些
            mFragments[FIRST] = findFragment(MultiFirstFragment.class);
            mFragments[SECOND] = findFragment(MultiSecondFragment.class);
            mFragments[THIRD] = findFragment(MultiThirdFragment.class);
            mFragments[FOURTH] = findFragment(MultiFourthFragment.class);
        }

        initView();
    }

    @Override
    protected FragmentAnimator onCreateFragmentAnimator() {
        return super.onCreateFragmentAnimator();
    }

    private void initView() {
        mBottomBar = (BottomBar) findViewById(R.id.bottomBar);

        mBottomBar.addItem(new BottomBarTab(this, R.drawable.ic_home_white_24dp))
                .addItem(new BottomBarTab(this, R.drawable.ic_discover_white_24dp))
                .addItem(new BottomBarTab(this, R.drawable.ic_message_white_24dp))
                .addItem(new BottomBarTab(this, R.drawable.ic_account_circle_white_24dp));

        mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, int prePosition) {
                showHideFragment(mFragments[position], mFragments[prePosition]);
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {
                SupportFragment currentFragment = mFragments[position];
                int count = currentFragment.getChildFragmentManager().getBackStackEntryCount();

                // 如果不在该类别Fragment的主页,则回到主页;
                if (count > 1) {
                    if (currentFragment instanceof MultiFirstFragment) {
                        currentFragment.popToChild(FirstHomeFragment.class, false);
                    } else if (currentFragment instanceof MultiSecondFragment) {
                        currentFragment.popToChild(ViewPagerFragment.class, false);
                    }
                    return;
                }


                // 这里推荐使用EventBus来实现 -> 解耦
                if (count == 1) {
                    EventBus.getDefault().post(new TabSelectedEvent(position));
                }
            }
        });
    }

    @Override
    public void onBackPressedSupport() {
        // 对于 4个类别的主Fragment内的回退back逻辑,已经在其onBackPressedSupport里各自处理了
        super.onBackPressedSupport();
    }

    @Override
    public void onBackToFirstFragment() {
        mBottomBar.setCurrentItem(0);
    }

    /**
     * 这里暂没实现,忽略
     */
//    @Subscribe
//    public void onHiddenBottombarEvent(boolean hidden) {
//        if (hidden) {
//            mBottomBar.hide();
//        } else {
//            mBottomBar.show();
//        }
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }
}
