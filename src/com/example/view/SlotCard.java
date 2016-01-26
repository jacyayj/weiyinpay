package com.example.view;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.example.controll.DeviceController;
import com.example.cz.Const.PinWKIndexConst;
import com.example.cz.DeviceControllerImpl;
import com.example.cz.R;
import com.example.modle.TradeModle;
import com.example.modle.TransferListener;
import com.example.untils.ByteUtils;
import com.example.untils.AllUtils;
import com.example.untils.LocalDOM;
import com.example.untils.MyHandler;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.newland.mtype.ModuleType;
import com.newland.mtype.module.common.emv.EmvTransController;
import com.newland.mtype.module.common.emv.EmvTransInfo;
import com.newland.mtype.module.common.emv.SecondIssuanceRequest;
import com.newland.mtype.module.common.pin.WorkingKey;
import com.newland.mtype.module.common.swiper.SwipResult;
import com.newland.mtype.module.external.me11.ME11SwipResult;
import com.newland.mtype.tlv.TLVPackage;
import com.newland.mtype.util.Dump;
import com.newland.mtype.util.ISOUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
	/**
	 * 音频卡刷卡界面
	 * @author jacyayj
	 *
	 */
public class SlotCard extends Activity {
	private Boolean processing = false;
	private DeviceController controller = null;
	private String deviceInteraction = null;
	private ImageView ds[] = null;
	private Dialog dialog = null;
	private Dialog dialog2 = null;
	private EditText pwd = null;
	private Button cencle = null;
	private Button ok = null;
	private String pass = null;
	private Window dialogWindow = null;
	private TextView money = null;
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	private long mTime = 0;
	private String cardnumber = null;
	private String secondcardno = null;
	private String thridcardno = null;
	
	private List<Integer> TAGS_55 = null;
	private TradeModle modle = null;
	@SuppressLint("HandlerLeak")
	MyHandler handler = new MyHandler(SlotCard.this){
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x01) {}
		};  
	};
	/**
	 * 页面初始化
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.solt_card);
		ViewUtils.inject(this);
		modle = TradeModle.getInstance();
		controller = DeviceControllerImpl.getInstance();
		TAGS_55 = new ArrayList<Integer>();
		slot();
		inPut();
	}
	/**
	 * 监控页面状态，页面结束时断开设备连接
	 */
	@Override
	protected void onDestroy() {
		controller.disConnect();
		controller.destroy();
		super.onDestroy();
	}
	/**
	 * 页面点击事件
	 * @param v	触发点击事件的控件
	 */
	@OnClick({R.id.slot_back})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.slot_back : finish();
			
			break;

		default:
			break;
		}
	}
	/**
	 * 刷卡操作
	 */
	private void slot() {
		if (processing) {
			appendInteractiveInfoAndShow("设备当前仅能执行撤消操作...");
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						AllUtils.startProgressDialog(SlotCard.this,"刷卡中");
					}
				});
				btnStateToProcessing();
				try {
					//						connectDevice();
					appendInteractiveInfoAndShow("请刷卡或插卡...");
					Date curDate = new Date(System.currentTimeMillis());// 获取当前时间,建议从后台获取(用于PBOC交易)
					String strDate = formatter.format(curDate);
					ME11SwipResult swipRslt = null;
					swipRslt = controller.swipCard_me11(ByteUtils.hexString2ByteArray(strDate.substring(2)), 30000L, TimeUnit.MILLISECONDS);
					if (swipRslt == null) {
						appendInteractiveInfoAndShow("刷卡撤销");
						btnStateInitFinished();
						return;
					}
					try {
						System.out.println("hashID"+swipRslt.getAccount().getAcctHashId());
					} catch (Exception e) {
						System.out.println("hashID为空");
					}
					ModuleType[] moduleType = swipRslt.getReadModels();
					if (moduleType[0] == ModuleType.COMMON_SWIPER) {
						byte[] firstTrack = swipRslt.getFirstTrackData();
						byte[] secondTrack = swipRslt.getSecondTrackData();
						byte[] thirdTrack = swipRslt.getThirdTrackData();
						cardnumber = swipRslt.getAccount().getAcctNo();
						String first = (firstTrack ==null?"无":Dump.getHexDump(firstTrack));
						secondcardno = (secondTrack ==null?"无":Dump.getHexDump(secondTrack));
						thridcardno = (thirdTrack ==null?"无":Dump.getHexDump(thirdTrack));
						String extingo = swipRslt.getExtInfo() ==null?"无":Dump.getHexDump(swipRslt.getExtInfo());
						@SuppressWarnings("deprecation")
						String track = swipRslt.getTrackDatas() == null?"无":Dump.getHexDump( swipRslt.getTrackDatas());
						System.out.println("磁条卡\n卡号："+cardnumber+"\n一磁道："+first+"\n二磁道："+secondcardno+"\n三磁道："+thridcardno+"\ngetServiceCode："+swipRslt.getServiceCode()
								+"\nValidDate："+swipRslt.getValidDate()+"\nAcctHashId："+swipRslt.getAccount().getAcctHashId()+"\nExtInfo："+extingo+"\nTrackDatas："+track
								);
						runOnUiThread(new Runnable() {
							public void run() {
								if ("addacc".equals(LocalDOM.getinstance().read(getBaseContext(), "user","from"))) {
									modle.setId(cardnumber);
									LocalDOM.getinstance().write(getBaseContext(), "user", "from", "slot");
									finish();
								}else if("pay".equals(LocalDOM.getinstance().read(getBaseContext(), "user","from"))){
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											dialog.show();
										}
									});
								}else if("visa".equals(LocalDOM.getinstance().read(getBaseContext(), "user","from"))){
									modle.setId(cardnumber);
									startActivity(new Intent(SlotCard.this,VisaAttest1.class));
									finish();
								}
							}
						});
//						handler.sendEmptyMessage(0x01);
						appendInteractiveInfoAndShow("刷卡成功");
					} else if (moduleType[0] == ModuleType.COMMON_ICCARD) {
						appendInteractiveInfoAndShow("刷卡中");
						mTime = System.currentTimeMillis();
						controller.startEmv(new BigDecimal("30.00"), new SimpleTransferListener());
					}
				} catch (Exception e) {
					appendInteractiveInfoAndShow("磁条卡刷卡失败！" + e.getMessage());
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							AllUtils.stopProgressDialog();
						}
					});
					btnStateInitFinished();
					return;
				}
				btnStateInitFinished();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						AllUtils.stopProgressDialog();
					}
				});
			}
		}).start();
	}
	/**
	 * 向用户展示信息
	 * @param string	需要展示的信息
	 */
	private void appendInteractiveInfoAndShow(String string) {
		deviceInteraction = string;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getBaseContext(), deviceInteraction, Toast.LENGTH_SHORT).show();
			}
		});
	}
	/**
	 * 设置成初始化结束状态
	 * @since ver1.0
	 */
	private void btnStateInitFinished() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				processing = false;
			}
		});
	}
	private void btnStateToProcessing() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				processing = true;
			}
		});
	}
	private void processingFinished() {
		synchronized (SlotCard.this.processing) {
			SlotCard.this.processing = false;
		}
	}
	/**
	 * 芯片卡刷卡
	 * @author jacyayj
	 *
	 */
	private class SimpleTransferListener implements TransferListener {
		@Override
		public void onEmvFinished(boolean arg0, EmvTransInfo context) throws Exception {
			AllUtils.stopProgressDialog();
			//				System.out.println("emv交易结束");
			appendInteractiveInfoAndShow("刷卡完成！" + ""/*context.externalToString()*/);
			//				appendInteractiveInfoAndShow(">>>>交易完成，卡号:" + context.getCardNo() + ",卡序列号:" + context.getCardSequenceNumber());
			//				appendInteractiveInfoAndShow(">>>>交易完成，密码:" + ISOUtils.hexString(context.getOnLinePin()));
			mTime = System.currentTimeMillis() - mTime;
			processingFinished();
			//				appendInteractiveInfoAndShow("交易耗时:" + mTime/1000+"."+mTime%1000 + "s");
//			handler.sendEmptyMessage(0x01);
			runOnUiThread(new Runnable() {
				public void run() {
					if ("addacc".equals(LocalDOM.getinstance().read(getBaseContext(), "user","from"))) {
						modle.setId(cardnumber);
						LocalDOM.getinstance().write(getBaseContext(), "user", "from", "slot");
						finish();
					}else if("pay".equals(LocalDOM.getinstance().read(getBaseContext(), "user","from"))){
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								dialog.show();
							}
						});
					}else if("visa".equals(LocalDOM.getinstance().read(getBaseContext(), "user","from"))){
						modle.setId(cardnumber);
						startActivity(new Intent(SlotCard.this,VisaAttest1.class));
						finish();
					}
				}
			});
		}
		@Override
		public void onError(EmvTransController arg0, Exception arg1) {
			//				System.out.println("emv交易失败");
			appendInteractiveInfoAndShow("芯片卡刷卡失败");
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					AllUtils.stopProgressDialog();
				}
			});
			processingFinished();
		}
		@Override
		public void onFallback(EmvTransInfo arg0) throws Exception {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					AllUtils.stopProgressDialog();
				}
			});
			System.out.println("交易降级");
			appendInteractiveInfoAndShow("交易降级");
			//				startSwipTransfer();
			processingFinished();
		}
		@Override
		public void onRequestOnline(EmvTransController controller, EmvTransInfo context) throws Exception {
			//				System.out.println("开启联机交易");
			//				appendInteractiveInfoAndShow("开启联机交易：" + ""/*context.externalToString()*/);
			//				appendInteractiveInfoAndShow(">>>>请求在线交易处理");
			//				appendInteractiveInfoAndShow("		95：" + (context.getTerminalVerificationResults() == null ? "无返回" : Dump.getHexDump(context.getTerminalVerificationResults())));
			//				appendInteractiveInfoAndShow("		9f26:" + (context.getAppCryptogram() == null ? "无返回" : Dump.getHexDump(context.getAppCryptogram())));
			//				appendInteractiveInfoAndShow("		9f34:" + (context.getCvmRslt() == null ? "无返回" : Dump.getHexDump(context.getCvmRslt())));
			//				appendInteractiveInfoAndShow(">>>>卡号:" + context.getCardNo() + ",卡序列号:" + context.getCardSequenceNumber());
			//				appendInteractiveInfoAndShow(">>>>二磁等效信息:" + (context.getTrack_2_eqv_data() == null ? "无返回" : Dump.getHexDump(context.getTrack_2_eqv_data())));
			//				twocardnumber = Dump.getHexDump(context.getTrack_2_eqv_data());
			//				cardnumber = context.getCardNo();
			//				JtL.toast(getBaseContext(), cardnumber);
			//				handler.sendEmptyMessage(0x01);
			//				appendInteractiveInfoAndShow(">>>>密码:" + context.getOnLinePin());
			cardnumber = context.getCardNo();
			secondcardno =  context.getTrack_2_eqv_data()==null?"":Dump.getHexDump(context.getTrack_2_eqv_data());
			System.out.println("芯片卡\n"+"卡号"+cardnumber+"\n"+"二磁道"+secondcardno+"\n密码"+"\n三磁道\n");
			TAGS_55.add(0x9f26);
			TAGS_55.add(0x9F27);
			TAGS_55.add(0x9F10);
			TAGS_55.add(0x9F37);
			TAGS_55.add(0x9F36);
			TAGS_55.add(0x95);
			TAGS_55.add(0x9A);
			TAGS_55.add(0x9C);
			TAGS_55.add(0x9F02);
			TAGS_55.add(0x5F2A);
			TAGS_55.add(0x82);
			TAGS_55.add(0x9F1A);
			TAGS_55.add(0x9F03);
			TAGS_55.add(0x9F33);
			TAGS_55.add(0x9F34);
			TAGS_55.add(0x9F35);
			TAGS_55.add(0x9F1E);
			TAGS_55.add(0x84);
			TAGS_55.add(0x9F09);
			TAGS_55.add(0x9F41);
			TAGS_55.add(0x9F63);
			
			TAGS_55.add(0x9F40);
			TAGS_55.add(0x9F06);
			TAGS_55.add(0x4F);
			TAGS_55.add(0x9B);
			TAGS_55.add(0x97);
			TAGS_55.add(0x9F12);
			TAGS_55.add(0x50);
			TAGS_55.add(0x5A);
			TAGS_55.add(0x5F2A);
			TAGS_55.add(0x9F21);
			TAGS_55.add(0x5F24);
			TAGS_55.add(0x5F34);
			TAGS_55.add(0x9F39);
			TAGS_55.add(0xDF75);
			TAGS_55.add(0xDF76);
			TLVPackage tlvPackage = context.setExternalInfoPackage(TAGS_55);
			System.out.println("55域"+ISOUtils.hexString(tlvPackage.pack()));
//			System.out.println("芯片卡：\nexternal："+context.externalToString()+"\nAmountAuthorisedNumeric："+context.getAmountAuthorisedNumeric()+"\nAmountOtherNumeric："+context.getAmountOtherNumeric()+"\nApp_preferred_name："+context.getApp_preferred_name()
//					+"\nApplication_label："+context.getApplication_label()+"\n"+context.getAuthorisationResponseCode()+"\n有效期："+context.getCardExpirationDate()+"\n卡号："+context.getCardNo()+"\n卡序列号："+context.getCardSequenceNumber()
//					+"\nCryptogramInformationData："+context.getCryptogramInformationData()+"\nInterface_device_serial_number："+context.getInterface_device_serial_number()+"\nPbocCardFunds:"+context.getPbocCardFunds()+"\nTerminalCountryCode："+context.getTerminalCountryCode()
//					+"\nTerminalReadingTime："+context.getTerminalReadingTime()+"\nTerminalType:"+context.getTerminalType()+"\nTransaction_time："+context.getTransaction_time()+"\nTransactionCurrencyCode："+context.getTransactionCurrencyCode()+"\nTransactionDate："+context.getTransactionDate()
//					+"\nExecuteRslt："+context.getExecuteRslt()+"\nTransactionType:"+context.getTransactionType()+"\nAdditionalTerminalCapabilities："+Dump.getHexDump(context.getAdditionalTerminalCapabilities())+"\nAid:"+Dump.getHexDump(context.getAid())+"\nAid_card："+Dump.getHexDump(context.getAid_card())+"\nAppCryptogram："+Dump.getHexDump(context.getAppCryptogram())
//					+"\nApplicationInterchangeProfile："+Dump.getHexDump(context.getApplicationInterchangeProfile())+"\nAppTransactionCounter："+Dump.getHexDump(context.getAppTransactionCounter())+"\nAppVersionNumberTerminal："+Dump.getHexDump(context.getAppVersionNumberTerminal())+"\nCardProductIdatification："+context.getCardProductIdatification()
//					+"\nChipSerialNo："+context.getChipSerialNo()+"\nCvmRslt："+Dump.getHexDump(context.getCvmRslt())+"\nDedicatedFileName："+Dump.getHexDump(context.getDedicatedFileName())+"\nEcIssuerAuthorizationCode："+context.getEcIssuerAuthorizationCode()+"\nErrorcode："+Dump.getHexDump(context.getErrorcode())
//					+"\nExternal："+context.getExternal(0)+"\nExternalPackage："+context.getExternalPackage()+"\nInner_transaction_type："+context.getInner_transaction_type()+"\nIssuerApplicationData："+Dump.getHexDump(context.getIssuerApplicationData())+"\nIssuerAuthenticationData："+context.getIssuerAuthenticationData()
//					+"\nIssuerScriptTemplate1："+context.getIssuerScriptTemplate1()+"\nIssuerScriptTemplate2："+context.getIssuerScriptTemplate2()+"\nKsn："+context.getKsn()+"\nOnLinePin："+context.getOnLinePin()+"\nRelativeTags："+context.getRelativeTags()+"\nScriptExecuteRslt："+context.getScriptExecuteRslt()
//					+"\nSessionKeyData："+context.getSessionKeyData()+"\nTerminal_capabilities："+Dump.getHexDump(context.getTerminal_capabilities())+"\nTerminalVerificationResults："+Dump.getHexDump(context.getTerminalVerificationResults())+"\n二磁道："+Dump.getHexDump(context.getTrack_2_eqv_data())+"\nTransaction_status_information："+Dump.getHexDump(context.getTransaction_status_information())
//					+"\nUnpredictableNumber："+Dump.getHexDump(context.getUnpredictableNumber())
//					);
			SecondIssuanceRequest request = new SecondIssuanceRequest();
			request.setAuthorisationResponseCode("00");
			controller.secondIssuance(request);
		}
		@Override
		public void onRequestPinEntry(EmvTransController arg0, EmvTransInfo arg1) throws Exception {
			runOnUiThread(new Runnable() {
				public void run() {
					AllUtils.stopProgressDialog();
				}
			});
			System.out.println("错误的事件返回，不可能要求密码输入");
			appendInteractiveInfoAndShow("错误的事件返回，不可能要求密码输入！");
			arg0.cancelEmv();
		}
		@Override
		public void onRequestSelectApplication(EmvTransController arg0, EmvTransInfo arg1) throws Exception {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					AllUtils.stopProgressDialog();
				}
			});
			System.out.println("错误的事件返回，不可能要求应用选择！");
			appendInteractiveInfoAndShow("错误的事件返回，不可能要求应用选择！");
			arg0.cancelEmv();
		}
		@Override
		public void onRequestTransferConfirm(EmvTransController arg0, EmvTransInfo arg1) throws Exception {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					AllUtils.stopProgressDialog();
				}
			});
			appendInteractiveInfoAndShow("错误的事件返回，不可能要求交易确认！");
			arg0.cancelEmv();
		}
		@Override
		public void onSwipMagneticCard(SwipResult swipRslt) {
			//				startSwipTransfer();
		}
		@Override
		public void onOpenCardreaderCanceled() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					AllUtils.stopProgressDialog();
				}
			});
			appendInteractiveInfoAndShow("用户撤销刷卡操作！");
			processingFinished();
		}
	}
	@SuppressWarnings("deprecation")
	private void inPut() {  
		dialog = new Dialog(SlotCard.this,R.style.mydialog2);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.input_pwd);
		dialogWindow = dialog.getWindow();
		cencle = (Button) dialogWindow.findViewById(R.id.pwd_cencle);
		ok = (Button) dialogWindow.findViewById(R.id.pwd_ok);
		pwd = (EditText) dialogWindow.findViewById(R.id.pwd_pwd);
		money = (TextView) dialogWindow.findViewById(R.id.pwd_money);
		money.setText("￥"+modle.getPrice()+"元");
		ds = new ImageView[6];
		for (int i = 0; i < ds.length; i++) {
			ds[i] = (ImageView) dialogWindow.findViewById(R.id.pwd_1+i);
		}
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		lp.width =  (int) (dialogWindow.getWindowManager().getDefaultDisplay().getWidth()*0.85); // 宽度
		lp.height =  (int) (dialogWindow.getWindowManager().getDefaultDisplay().getHeight()*0.35); // 高度
		dialogWindow.setAttributes(lp);
		cencle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (pass.length() == 6) {
					String pwd = "";
					try {
						modle.setId(cardnumber);
						byte[] key = controller.encrypt(new WorkingKey(PinWKIndexConst.DEFAULT_PIN_WK_INDEX),ByteUtils.hexString2ByteArray(pass));
						for(int i=0; i<key.length; i++)
							pwd += String.format("%02X", key[i]);
						System.out.println("加密后密码"+pwd);
					} catch (Exception e) {
						AllUtils.toast(getBaseContext(), "密码加密失败");
					}
					modle.setPwd(pwd);
					modle.setSencondno(secondcardno);
					modle.setThirdno(thridcardno);
					dialog.dismiss();
					//						handler.sendEmptyMessage(0x01);
					startActivity(new Intent().setClass(getBaseContext(),Sign.class));
					finish();
				}else {
					Toast.makeText(getBaseContext(), "请输入6位数的密码", Toast.LENGTH_SHORT).show();
				}
			}
		});
		pwd.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				pass = s.toString();
				switch (s.length()) {
				case 0:	ds[0].setVisibility(View.INVISIBLE);
				ds[1].setVisibility(View.INVISIBLE);
				ds[2].setVisibility(View.INVISIBLE);
				ds[3].setVisibility(View.INVISIBLE);
				ds[4].setVisibility(View.INVISIBLE);
				ds[5].setVisibility(View.INVISIBLE);
				break;
				case 1:	ds[0].setVisibility(View.VISIBLE);
				ds[1].setVisibility(View.INVISIBLE);
				ds[2].setVisibility(View.INVISIBLE);
				ds[3].setVisibility(View.INVISIBLE);
				ds[4].setVisibility(View.INVISIBLE);
				ds[5].setVisibility(View.INVISIBLE);		
				break;
				case 2:	ds[0].setVisibility(View.VISIBLE);
				ds[1].setVisibility(View.VISIBLE);
				ds[2].setVisibility(View.INVISIBLE);
				ds[3].setVisibility(View.INVISIBLE);
				ds[4].setVisibility(View.INVISIBLE);
				ds[5].setVisibility(View.INVISIBLE);
				break;
				case 3:	ds[0].setVisibility(View.VISIBLE);
				ds[1].setVisibility(View.VISIBLE);
				ds[2].setVisibility(View.VISIBLE);
				ds[3].setVisibility(View.INVISIBLE);
				ds[4].setVisibility(View.INVISIBLE);
				ds[5].setVisibility(View.INVISIBLE);
				break;
				case 4:	ds[0].setVisibility(View.VISIBLE);
				ds[1].setVisibility(View.VISIBLE);
				ds[2].setVisibility(View.VISIBLE);
				ds[3].setVisibility(View.VISIBLE);
				ds[4].setVisibility(View.INVISIBLE);
				ds[5].setVisibility(View.INVISIBLE);
				break;
				case 5:	ds[0].setVisibility(View.VISIBLE);
				ds[1].setVisibility(View.VISIBLE);
				ds[2].setVisibility(View.VISIBLE);
				ds[3].setVisibility(View.VISIBLE);
				ds[4].setVisibility(View.VISIBLE);
				ds[5].setVisibility(View.INVISIBLE);
				break;
				case 6:	ds[0].setVisibility(View.VISIBLE);
				ds[1].setVisibility(View.VISIBLE);
				ds[2].setVisibility(View.VISIBLE);
				ds[3].setVisibility(View.VISIBLE);
				ds[4].setVisibility(View.VISIBLE);
				ds[5].setVisibility(View.VISIBLE);
				break;
				default:
					break;
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}
	@SuppressWarnings({ "deprecation", "unused" })
	private void pwdWrong() {
		dialog2 = new Dialog(SlotCard.this,R.style.mydialog2);
		dialog2.setCancelable(false);
		dialog2.setContentView(R.layout.pwd_wrong);
		dialogWindow = dialog2.getWindow();

		ok = (Button) dialogWindow.findViewById(R.id.wrong_ok);

		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				startActivity(new Intent(SlotCard.this,Sign.class));
				finish();
			}
		});
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		lp.width =  (int) (dialogWindow.getWindowManager().getDefaultDisplay().getWidth()*0.85); // 宽度
		lp.height =  (int) (dialogWindow.getWindowManager().getDefaultDisplay().getHeight()*0.15); // 高度
		dialogWindow.setAttributes(lp);
		dialog2.show();
	}
}
