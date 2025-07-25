package com.iyuba.music.activity.pay;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.adapter.MaterialDialogAdapter;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.entity.user.UserInfoOp;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.request.account.PayForAppRequest;
import com.iyuba.music.request.account.PayRequest;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;
import com.iyuba.music.widget.recycleview.MyLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 10202 on 2015/12/29.
 */
public class BuyVipActivity extends BaseActivity {
    private final static int ONLINE_PAY_CODE = 101;
    private final static int[] PAY_GOODS = {200, 600, 1000, 2000, 1100};
    private final static int[] PAY_MONTH = {1, 3, 6, 12, 0};
    private static final String[] PAY_MONEY = {"30", "88", "158", "298", "199"};
    private int payType;
    private UserInfo userInfo;
    private MaterialRippleLayout month, threeMonth, halfYear, year, app, payWay;
    private TextView vipUpdateText, vipIyubi, vipDeadline, vipName;
    private ImageView vipStatus;
    private CircleImageView vipPhoto;

    private Button rl_buyforevervip, btn_buyapp1, btn_buyapp2, btn_buyapp3, btn_buyapp4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_vip);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    public void onResume() {
        super.onResume();
        userInfo = AccountManager.getInstance().getUserInfo();
        changeUIResumeByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        month = (MaterialRippleLayout) findViewById(R.id.vip_month);
        threeMonth = (MaterialRippleLayout) findViewById(R.id.vip_three_month);
        halfYear = (MaterialRippleLayout) findViewById(R.id.vip_half_year);
        year = (MaterialRippleLayout) findViewById(R.id.vip_year);
        app = (MaterialRippleLayout) findViewById(R.id.vip_app);
        payWay = (MaterialRippleLayout) findViewById(R.id.vip_buy_way);

        vipUpdateText = (TextView) findViewById(R.id.vip_update_text);
        vipIyubi = (TextView) findViewById(R.id.vip_iyubi);
        vipDeadline = (TextView) findViewById(R.id.vip_deadline);
        vipName = (TextView) findViewById(R.id.vip_name);
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);

        vipStatus = (ImageView) findViewById(R.id.vip_status);
        vipPhoto = (CircleImageView) findViewById(R.id.vip_photo);


        rl_buyforevervip = (Button) findViewById(R.id.rl_buyforevervip);
        btn_buyapp1 = (Button) findViewById(R.id.btn_buyapp1);
        btn_buyapp2 = (Button) findViewById(R.id.btn_buyapp2);
        btn_buyapp3 = (Button) findViewById(R.id.btn_buyapp3);
        btn_buyapp4 = (Button) findViewById(R.id.btn_buyapp4);
    }

    @Override
    protected void setListener() {
        super.setListener();
        btn_buyapp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay(0);
            }
        });
        btn_buyapp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay(1);
            }
        });
        btn_buyapp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay(2);
            }
        });
        btn_buyapp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay(3);
            }
        });
        rl_buyforevervip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay(4);
            }
        });
        payWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payWayDialog();
            }
        });
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, BuyIyubiActivity.class));
            }
        });
    }

    private void pay(int type) {
        if (payType == 1) {
            payByIyubiDialog(type);
        } else {
            String goodsDetail;
            switch (type) {
                case 0:
                    goodsDetail = getString(R.string.vip_month);
                    break;
                case 1:
                    goodsDetail = getString(R.string.vip_three_month);
                    break;
                case 2:
                    goodsDetail = getString(R.string.vip_half_year);
                    break;
                case 3:
                    goodsDetail = getString(R.string.vip_year);
                    break;
                case 4:
                    goodsDetail = getString(R.string.vip_app);
                    break;
                default:
                    goodsDetail = getString(R.string.vip_month);
                    break;
            }
            PayActivity.launch(this, goodsDetail.split("-")[0], PAY_MONEY[type],
                    String.valueOf(PAY_MONTH[type]), getProductId(type), ONLINE_PAY_CODE);
        }
    }

    @NonNull
    private String getProductId(int type) {
        String productId = "0";
        switch (type) {
            case 4:
                productId = "10";
                break;
            default:
                break;
        }
        return productId;
    }

    private void payByIyubiDialog(final int pos) {
        final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
        materialDialog.setTitle(R.string.vip_title).setMessage(context.getString(R.string.vip_buy_alert, PAY_GOODS[pos]));
        materialDialog.setNegativeButton(R.string.app_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.setPositiveButton(R.string.app_buy, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
                buy(pos);
            }
        });
        materialDialog.show();
    }

    private void payWayDialog() {
        final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
        materialDialog.setTitle(R.string.vip_buy_way);
        View root = View.inflate(context, R.layout.recycleview, null);
        RecyclerView payWayList = (RecyclerView) root.findViewById(R.id.listview);
        List<String> payWays = new ArrayList<>();
        payWays.add(context.getString(R.string.vip_buy_money));
        payWays.add(context.getString(R.string.vip_buy_iyubi));
        MaterialDialogAdapter adapter = new MaterialDialogAdapter(context, payWays);
        adapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                payType = position;
                materialDialog.dismiss();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        adapter.setSelected(payType);
        payWayList.setAdapter(adapter);
        payWayList.setLayoutManager(new MyLinearLayoutManager(context));
        payWayList.addItemDecoration(new DividerItemDecoration());
        materialDialog.setContentView(root);
        materialDialog.setPositiveButton(R.string.app_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.vip_title);
        toolbarOper.setText(R.string.vip_recharge);
    }

    private void changeUIResumeByPara() {
        ImageUtil.loadAvatar(AccountManager.getInstance().getUserId(), vipPhoto);
        if (userInfo.getVipStatus() != null && Integer.parseInt(userInfo.getVipStatus()) >= 1) {
            vipStatus.setImageResource(R.drawable.vip);
            vipUpdateText.setText(R.string.vip_update);
            vipDeadline.setText(context.getString(R.string.vip_deadline, userInfo.getDeadline()));
        } else {
            vipStatus.setImageResource(R.drawable.unvip);
            vipUpdateText.setText(R.string.vip_buy);
            vipDeadline.setText(context.getString(R.string.vip_undeadline));
        }
        vipName.setText(userInfo.getUsername());
        vipIyubi.setText(context.getString(R.string.vip_iyubi, userInfo.getIyubi()));
    }

    private void buy(int type) {
        if (Integer.parseInt(AccountManager.getInstance().getUserInfo().getIyubi()) < PAY_GOODS[type]) {
            final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
            materialDialog.setTitle(R.string.vip_huge).setMessage(R.string.vip_iyubi_not_enough);
            materialDialog.setPositiveButton(R.string.app_buy, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, BuyIyubiActivity.class));
                    materialDialog.dismiss();
                }
            });
            materialDialog.setNegativeButton(R.string.app_cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    materialDialog.dismiss();
                }
            });
            materialDialog.show();
        } else if (type != 4) {
            PayRequest.exeRequest(PayRequest.generateUrl(new String[]{AccountManager.getInstance().getUserId(),
                    String.valueOf(PAY_GOODS[type]), String.valueOf(PAY_MONTH[type])}), new IProtocolResponse() {
                @Override
                public void onNetError(String msg) {
                    CustomToast.getInstance().showToast(msg);
                }

                @Override
                public void onServerError(String msg) {
                    CustomToast.getInstance().showToast(msg);
                }

                @Override
                public void response(Object object) {
                    BaseApiEntity apiEntity = (BaseApiEntity) object;
                    if (BaseApiEntity.isSuccess(apiEntity)) {
                        userInfo = (UserInfo) apiEntity.getData();
                        new UserInfoOp().saveData(userInfo);
                        AccountManager.getInstance().setUserInfo(userInfo);
                        CustomToast.getInstance().showToast(context.getString(R.string.vip_buy_success,
                                userInfo.getIyubi()));
                        changeUIResumeByPara();
                    } else {
                        CustomToast.getInstance().showToast(context.getString(R.string.vip_buy_fail,
                                apiEntity.getMessage()));
                    }
                }
            });
        } else {
            PayForAppRequest.exeRequest(PayForAppRequest.generateUrl(new String[]
                    {AccountManager.getInstance().getUserId(), String.valueOf(PAY_GOODS[type])}), new IProtocolResponse() {
                @Override
                public void onNetError(String msg) {
                    CustomToast.getInstance().showToast(msg);
                }

                @Override
                public void onServerError(String msg) {
                    CustomToast.getInstance().showToast(msg);
                }

                @Override
                public void response(Object object) {
                    BaseApiEntity baseApiEntity = (BaseApiEntity) object;
                    if (BaseApiEntity.isSuccess(baseApiEntity)) {
                        userInfo = (UserInfo) baseApiEntity.getData();
                        new UserInfoOp().saveData(userInfo);
                        AccountManager.getInstance().setUserInfo(userInfo);
                        CustomToast.getInstance().showToast(context.getString(R.string.vip_buy_success,
                                userInfo.getIyubi()));
                        changeUIResumeByPara();
                    } else {
                        CustomToast.getInstance().showToast(context.getString(R.string.vip_buy_fail,
                                baseApiEntity.getMessage()));
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ONLINE_PAY_CODE && resultCode == RESULT_OK) {              // 刷新vip时长
            AccountManager.getInstance().refreshVipStatus();
        }
    }
}
