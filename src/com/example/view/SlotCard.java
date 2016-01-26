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
	 * ��Ƶ��ˢ������
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
	 * ҳ���ʼ��
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
	 * ���ҳ��״̬��ҳ�����ʱ�Ͽ��豸����
	 */
	@Override
	protected void onDestroy() {
		controller.disConnect();
		controller.destroy();
		super.onDestroy();
	}
	/**
	 * ҳ�����¼�
	 * @param v	��������¼��Ŀؼ�
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
	 * ˢ������
	 */
	private void slot() {
		if (processing) {
			appendInteractiveInfoAndShow("�豸��ǰ����ִ�г�������...");
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						AllUtils.startProgressDialog(SlotCard.this,"ˢ����");
					}
				});
				btnStateToProcessing();
				try {
					//						connectDevice();
					appendInteractiveInfoAndShow("��ˢ����忨...");
					Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��,����Ӻ�̨��ȡ(����PBOC����)
					String strDate = formatter.format(curDate);
					ME11SwipResult swipRslt = null;
					swipRslt = controller.swipCard_me11(ByteUtils.hexString2ByteArray(strDate.substring(2)), 30000L, TimeUnit.MILLISECONDS);
					if (swipRslt == null) {
						appendInteractiveInfoAndShow("ˢ������");
						btnStateInitFinished();
						return;
					}
					try {
						System.out.println("hashID"+swipRslt.getAccount().getAcctHashId());
					} catch (Exception e) {
						System.out.println("hashIDΪ��");
					}
					ModuleType[] moduleType = swipRslt.getReadModels();
					if (moduleType[0] == ModuleType.COMMON_SWIPER) {
						byte[] firstTrack = swipRslt.getFirstTrackData();
						byte[] secondTrack = swipRslt.getSecondTrackData();
						byte[] thirdTrack = swipRslt.getThirdTrackData();
						cardnumber = swipRslt.getAccount().getAcctNo();
						String first = (firstTrack ==null?"��":Dump.getHexDump(firstTrack));
						secondcardno = (secondTrack ==null?"��":Dump.getHexDump(secondTrack));
						thridcardno = (thirdTrack ==null?"��":Dump.getHexDump(thirdTrack));
						String extingo = swipRslt.getExtInfo() ==null?"��":Dump.getHexDump(swipRslt.getExtInfo());
						@SuppressWarnings("deprecation")
						String track = swipRslt.getTrackDatas() == null?"��":Dump.getHexDump( swipRslt.getTrackDatas());
						System.out.println("������\n���ţ�"+cardnumber+"\nһ�ŵ���"+first+"\n���ŵ���"+secondcardno+"\n���ŵ���"+thridcardno+"\ngetServiceCode��"+swipRslt.getServiceCode()
								+"\nValidDate��"+swipRslt.getValidDate()+"\nAcctHashId��"+swipRslt.getAccount().getAcctHashId()+"\nExtInfo��"+extingo+"\nTrackDatas��"+track
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
						appendInteractiveInfoAndShow("ˢ���ɹ�");
					} else if (moduleType[0] == ModuleType.COMMON_ICCARD) {
						appendInteractiveInfoAndShow("ˢ����");
						mTime = System.currentTimeMillis();
						controller.startEmv(new BigDecimal("30.00"), new SimpleTransferListener());
					}
				} catch (Exception e) {
					appendInteractiveInfoAndShow("������ˢ��ʧ�ܣ�" + e.getMessage());
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
	 * ���û�չʾ��Ϣ
	 * @param string	��Ҫչʾ����Ϣ
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
	 * ���óɳ�ʼ������״̬
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
	 * оƬ��ˢ��
	 * @author jacyayj
	 *
	 */
	private class SimpleTransferListener implements TransferListener {
		@Override
		public void onEmvFinished(boolean arg0, EmvTransInfo context) throws Exception {
			AllUtils.stopProgressDialog();
			//				System.out.println("emv���׽���");
			appendInteractiveInfoAndShow("ˢ����ɣ�" + ""/*context.externalToString()*/);
			//				appendInteractiveInfoAndShow(">>>>������ɣ�����:" + context.getCardNo() + ",�����к�:" + context.getCardSequenceNumber());
			//				appendInteractiveInfoAndShow(">>>>������ɣ�����:" + ISOUtils.hexString(context.getOnLinePin()));
			mTime = System.currentTimeMillis() - mTime;
			processingFinished();
			//				appendInteractiveInfoAndShow("���׺�ʱ:" + mTime/1000+"."+mTime%1000 + "s");
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
			//				System.out.println("emv����ʧ��");
			appendInteractiveInfoAndShow("оƬ��ˢ��ʧ��");
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
			System.out.println("���׽���");
			appendInteractiveInfoAndShow("���׽���");
			//				startSwipTransfer();
			processingFinished();
		}
		@Override
		public void onRequestOnline(EmvTransController controller, EmvTransInfo context) throws Exception {
			//				System.out.println("������������");
			//				appendInteractiveInfoAndShow("�����������ף�" + ""/*context.externalToString()*/);
			//				appendInteractiveInfoAndShow(">>>>�������߽��״���");
			//				appendInteractiveInfoAndShow("		95��" + (context.getTerminalVerificationResults() == null ? "�޷���" : Dump.getHexDump(context.getTerminalVerificationResults())));
			//				appendInteractiveInfoAndShow("		9f26:" + (context.getAppCryptogram() == null ? "�޷���" : Dump.getHexDump(context.getAppCryptogram())));
			//				appendInteractiveInfoAndShow("		9f34:" + (context.getCvmRslt() == null ? "�޷���" : Dump.getHexDump(context.getCvmRslt())));
			//				appendInteractiveInfoAndShow(">>>>����:" + context.getCardNo() + ",�����к�:" + context.getCardSequenceNumber());
			//				appendInteractiveInfoAndShow(">>>>���ŵ�Ч��Ϣ:" + (context.getTrack_2_eqv_data() == null ? "�޷���" : Dump.getHexDump(context.getTrack_2_eqv_data())));
			//				twocardnumber = Dump.getHexDump(context.getTrack_2_eqv_data());
			//				cardnumber = context.getCardNo();
			//				JtL.toast(getBaseContext(), cardnumber);
			//				handler.sendEmptyMessage(0x01);
			//				appendInteractiveInfoAndShow(">>>>����:" + context.getOnLinePin());
			cardnumber = context.getCardNo();
			secondcardno =  context.getTrack_2_eqv_data()==null?"":Dump.getHexDump(context.getTrack_2_eqv_data());
			System.out.println("оƬ��\n"+"����"+cardnumber+"\n"+"���ŵ�"+secondcardno+"\n����"+"\n���ŵ�\n");
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
			System.out.println("55��"+ISOUtils.hexString(tlvPackage.pack()));
//			System.out.println("оƬ����\nexternal��"+context.externalToString()+"\nAmountAuthorisedNumeric��"+context.getAmountAuthorisedNumeric()+"\nAmountOtherNumeric��"+context.getAmountOtherNumeric()+"\nApp_preferred_name��"+context.getApp_preferred_name()
//					+"\nApplication_label��"+context.getApplication_label()+"\n"+context.getAuthorisationResponseCode()+"\n��Ч�ڣ�"+context.getCardExpirationDate()+"\n���ţ�"+context.getCardNo()+"\n�����кţ�"+context.getCardSequenceNumber()
//					+"\nCryptogramInformationData��"+context.getCryptogramInformationData()+"\nInterface_device_serial_number��"+context.getInterface_device_serial_number()+"\nPbocCardFunds:"+context.getPbocCardFunds()+"\nTerminalCountryCode��"+context.getTerminalCountryCode()
//					+"\nTerminalReadingTime��"+context.getTerminalReadingTime()+"\nTerminalType:"+context.getTerminalType()+"\nTransaction_time��"+context.getTransaction_time()+"\nTransactionCurrencyCode��"+context.getTransactionCurrencyCode()+"\nTransactionDate��"+context.getTransactionDate()
//					+"\nExecuteRslt��"+context.getExecuteRslt()+"\nTransactionType:"+context.getTransactionType()+"\nAdditionalTerminalCapabilities��"+Dump.getHexDump(context.getAdditionalTerminalCapabilities())+"\nAid:"+Dump.getHexDump(context.getAid())+"\nAid_card��"+Dump.getHexDump(context.getAid_card())+"\nAppCryptogram��"+Dump.getHexDump(context.getAppCryptogram())
//					+"\nApplicationInterchangeProfile��"+Dump.getHexDump(context.getApplicationInterchangeProfile())+"\nAppTransactionCounter��"+Dump.getHexDump(context.getAppTransactionCounter())+"\nAppVersionNumberTerminal��"+Dump.getHexDump(context.getAppVersionNumberTerminal())+"\nCardProductIdatification��"+context.getCardProductIdatification()
//					+"\nChipSerialNo��"+context.getChipSerialNo()+"\nCvmRslt��"+Dump.getHexDump(context.getCvmRslt())+"\nDedicatedFileName��"+Dump.getHexDump(context.getDedicatedFileName())+"\nEcIssuerAuthorizationCode��"+context.getEcIssuerAuthorizationCode()+"\nErrorcode��"+Dump.getHexDump(context.getErrorcode())
//					+"\nExternal��"+context.getExternal(0)+"\nExternalPackage��"+context.getExternalPackage()+"\nInner_transaction_type��"+context.getInner_transaction_type()+"\nIssuerApplicationData��"+Dump.getHexDump(context.getIssuerApplicationData())+"\nIssuerAuthenticationData��"+context.getIssuerAuthenticationData()
//					+"\nIssuerScriptTemplate1��"+context.getIssuerScriptTemplate1()+"\nIssuerScriptTemplate2��"+context.getIssuerScriptTemplate2()+"\nKsn��"+context.getKsn()+"\nOnLinePin��"+context.getOnLinePin()+"\nRelativeTags��"+context.getRelativeTags()+"\nScriptExecuteRslt��"+context.getScriptExecuteRslt()
//					+"\nSessionKeyData��"+context.getSessionKeyData()+"\nTerminal_capabilities��"+Dump.getHexDump(context.getTerminal_capabilities())+"\nTerminalVerificationResults��"+Dump.getHexDump(context.getTerminalVerificationResults())+"\n���ŵ���"+Dump.getHexDump(context.getTrack_2_eqv_data())+"\nTransaction_status_information��"+Dump.getHexDump(context.getTransaction_status_information())
//					+"\nUnpredictableNumber��"+Dump.getHexDump(context.getUnpredictableNumber())
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
			System.out.println("������¼����أ�������Ҫ����������");
			appendInteractiveInfoAndShow("������¼����أ�������Ҫ���������룡");
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
			System.out.println("������¼����أ�������Ҫ��Ӧ��ѡ��");
			appendInteractiveInfoAndShow("������¼����أ�������Ҫ��Ӧ��ѡ��");
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
			appendInteractiveInfoAndShow("������¼����أ�������Ҫ����ȷ�ϣ�");
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
			appendInteractiveInfoAndShow("�û�����ˢ��������");
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
		money.setText("��"+modle.getPrice()+"Ԫ");
		ds = new ImageView[6];
		for (int i = 0; i < ds.length; i++) {
			ds[i] = (ImageView) dialogWindow.findViewById(R.id.pwd_1+i);
		}
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		lp.width =  (int) (dialogWindow.getWindowManager().getDefaultDisplay().getWidth()*0.85); // ���
		lp.height =  (int) (dialogWindow.getWindowManager().getDefaultDisplay().getHeight()*0.35); // �߶�
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
						System.out.println("���ܺ�����"+pwd);
					} catch (Exception e) {
						AllUtils.toast(getBaseContext(), "�������ʧ��");
					}
					modle.setPwd(pwd);
					modle.setSencondno(secondcardno);
					modle.setThirdno(thridcardno);
					dialog.dismiss();
					//						handler.sendEmptyMessage(0x01);
					startActivity(new Intent().setClass(getBaseContext(),Sign.class));
					finish();
				}else {
					Toast.makeText(getBaseContext(), "������6λ��������", Toast.LENGTH_SHORT).show();
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
		lp.width =  (int) (dialogWindow.getWindowManager().getDefaultDisplay().getWidth()*0.85); // ���
		lp.height =  (int) (dialogWindow.getWindowManager().getDefaultDisplay().getHeight()*0.15); // �߶�
		dialogWindow.setAttributes(lp);
		dialog2.show();
	}
}
