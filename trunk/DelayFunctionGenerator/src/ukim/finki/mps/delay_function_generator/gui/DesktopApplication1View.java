package ukim.finki.mps.delay_function_generator.gui;
/*
 * DesktopApplication1View.java
 */


import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;

import ukim.finki.mps.delay_fucntion_generator.CodeBuilder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;
import javax.swing.JList;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * The application's main frame.
 */
public class DesktopApplication1View extends FrameView {

    public DesktopApplication1View(SingleFrameApplication app) {
        super(app);

        initComponents();
        getFrame().setResizable(false);
        jRadioButton1.setSelected(true);

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }

    @Action
    public void showAboutBox() {
    	
    }

    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        A = new javax.swing.JCheckBox();
        B = new javax.swing.JCheckBox();
        C = new javax.swing.JCheckBox();
        D = new javax.swing.JCheckBox();
        E = new javax.swing.JCheckBox();
        H = new javax.swing.JCheckBox();
        L = new javax.swing.JCheckBox();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        lstMemory = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        txtMemory = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtHours = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtMins = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtSecs = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtmSecs = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtnSecs = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtStates = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtErrorPlus = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtErrorMinus = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtStateDuration = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtMessages = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtCode = new javax.swing.JTextArea();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        Stack = new javax.swing.ButtonGroup();

        mainPanel.setName("mainPanel"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(DesktopApplication1.class).getContext().getResourceMap(DesktopApplication1View.class);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        A.setText(resourceMap.getString("A.text")); // NOI18N
        A.setName("A"); // NOI18N

        B.setText(resourceMap.getString("B.text")); // NOI18N
        B.setName("B"); // NOI18N

        C.setText(resourceMap.getString("C.text")); // NOI18N
        C.setName("C"); // NOI18N

        D.setText(resourceMap.getString("D.text")); // NOI18N
        D.setName("D"); // NOI18N

        E.setText(resourceMap.getString("E.text")); // NOI18N
        E.setName("E"); // NOI18N

        H.setText(resourceMap.getString("H.text")); // NOI18N
        H.setName("H"); // NOI18N

        L.setText(resourceMap.getString("L.text")); // NOI18N
        L.setName("L"); // NOI18N

        Stack.add(jRadioButton1);
        jRadioButton1.setText(resourceMap.getString("jRadioButton1.text")); // NOI18N
        jRadioButton1.setName("jRadioButton1"); // NOI18N

        Stack.add(jRadioButton2);
        jRadioButton2.setText(resourceMap.getString("jRadioButton2.text")); // NOI18N
        jRadioButton2.setName("jRadioButton2"); // NOI18N

        Stack.add(jRadioButton3);
        jRadioButton3.setText(resourceMap.getString("jRadioButton3.text")); // NOI18N
        jRadioButton3.setName("jRadioButton3"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButton2)
                    .addComponent(jRadioButton3)
                    .addComponent(jRadioButton1)
                    .addComponent(jLabel1)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(A)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(B)
                                    .addGap(2, 2, 2))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(H)
                                        .addComponent(D))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(C)
                                .addComponent(E)
                                .addComponent(L))
                            .addGap(35, 35, 35))))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(A)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(B)
                    .addComponent(C))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(E)
                    .addComponent(D))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(L)
                    .addComponent(H))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 104, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton1)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        lstMemory.setName("lstMemory"); // NOI18N
        jScrollPane3.setViewportView(lstMemory);

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        txtMemory.setText(resourceMap.getString("txtMemory.text")); // NOI18N
        txtMemory.setName("txtMemory"); // NOI18N

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtMemory, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMemory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel3.setName("jPanel3"); // NOI18N

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel6.border.title"))); // NOI18N
        jPanel6.setName("jPanel6"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        txtHours.setText(resourceMap.getString("txtHours.text")); // NOI18N
        txtHours.setName("txtHours"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        txtMins.setText(resourceMap.getString("txtMins.text")); // NOI18N
        txtMins.setName("txtMins"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        txtSecs.setText(resourceMap.getString("txtSecs.text")); // NOI18N
        txtSecs.setName("txtSecs"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        txtmSecs.setText(resourceMap.getString("txtmSecs.text")); // NOI18N
        txtmSecs.setName("txtmSecs"); // NOI18N

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        txtnSecs.setText(resourceMap.getString("txtnSecs.text")); // NOI18N
        txtnSecs.setName("txtnSecs"); // NOI18N

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        txtStates.setEditable(false);
        txtStates.setText(resourceMap.getString("txtStates.text")); // NOI18N
        txtStates.setName("txtStates"); // NOI18N

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        txtErrorPlus.setText(resourceMap.getString("txtPlus.text")); // NOI18N
        txtErrorPlus.setName("txtPlus"); // NOI18N

        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        txtErrorMinus.setText(resourceMap.getString("txtMinus.text")); // NOI18N
        txtErrorMinus.setName("txtMinus"); // NOI18N

        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        txtStateDuration.setText(resourceMap.getString("txtLengthofState.text")); // NOI18N
        txtStateDuration.setName("txtLengthofState"); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtHours, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMins, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSecs, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtmSecs, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtStates, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addGap(55, 55, 55)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtErrorPlus, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtErrorMinus, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtnSecs, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtStateDuration, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtHours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtMins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtSecs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(txtmSecs, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(txtnSecs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtStates, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel13)
                    .addComponent(txtErrorPlus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(txtErrorMinus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(txtStateDuration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel12)
                .addContainerGap())
        );

        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 744, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel4.border.title"))); // NOI18N
        jPanel4.setName("jPanel4"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtMessages.setColumns(20);
        txtMessages.setEditable(false);
        txtMessages.setRows(5);
        txtMessages.setName("txtMessages"); // NOI18N
        jScrollPane2.setViewportView(txtMessages);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 834, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel5.border.title"))); // NOI18N
        jPanel5.setName("jPanel5"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        txtCode.setColumns(20);
        txtCode.setEditable(false);
        txtCode.setFont(resourceMap.getFont("txtCode.font")); // NOI18N
        txtCode.setRows(10000);
        txtCode.setName("txtCode"); // NOI18N
        jScrollPane1.setViewportView(txtCode);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 876, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mainPanelLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(DesktopApplication1.class).getContext().getActionMap(DesktopApplication1View.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 863, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 689, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>

    private long convertTostates() {
        int days = 0;//Integer.parseInt((txtDays.getText()==null||txtDays.getText().length()==0)?"0":txtDays.getText());
        int hours = Integer.parseInt((txtHours.getText()==null||txtHours.getText().length()==0)?"0":txtHours.getText());
        int mins = Integer.parseInt((txtMins.getText()==null||txtMins.getText().length()==0)?"0":txtMins.getText());
        int secs = Integer.parseInt((txtSecs.getText()==null||txtSecs.getText().length()==0)?"0":txtSecs.getText());
        int msecs = Integer.parseInt((txtmSecs.getText()==null||txtmSecs.getText().length()==0)?"0":txtmSecs.getText());
        int nsecs = Integer.parseInt((txtnSecs.getText()==null||txtnSecs.getText().length()==0)?"0":txtnSecs.getText());
        
        long total = days*86400*1000000000L+hours*3600*1000000000L+mins*60*1000000000L+secs*1000000000L+msecs*1000000L+nsecs*1000L;
        int state_dur = Integer.parseInt((txtStateDuration.getText()==null||txtStateDuration.getText().length()==0)?"1":txtStateDuration.getText());
       
        if ( state_dur == 0 ) {
            return 0;
        }
        else {
        	long res = total/(long)state_dur;
        	txtStates.setText(Long.toString(res));
        	return res;
         }
    }
    
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {
    	System.out.println("HERE");
        //call the function from here
    	long time = convertTostates();
        ArrayList<ukim.finki.mps.delay_fucntion_generator.Element> elements_available = new ArrayList<ukim.finki.mps.delay_fucntion_generator.Element>();
        if ( A.isSelected() ) {
        	elements_available.add(new ukim.finki.mps.delay_fucntion_generator.Element(ukim.finki.mps.delay_fucntion_generator.Element.REGISTER	,"A"));
        }
        if ( B.isSelected() ) {
        	elements_available.add(new ukim.finki.mps.delay_fucntion_generator.Element(ukim.finki.mps.delay_fucntion_generator.Element.REGISTER	,"B"));
        }
        if ( C.isSelected() ) {
        	elements_available.add(new ukim.finki.mps.delay_fucntion_generator.Element(ukim.finki.mps.delay_fucntion_generator.Element.REGISTER	,"C"));
        }
        if ( D.isSelected() ) {
        	elements_available.add(new ukim.finki.mps.delay_fucntion_generator.Element(ukim.finki.mps.delay_fucntion_generator.Element.REGISTER	,"D"));
        }
        if ( E.isSelected() ) {
        	elements_available.add(new ukim.finki.mps.delay_fucntion_generator.Element(ukim.finki.mps.delay_fucntion_generator.Element.REGISTER	,"E"));
        }
        if ( H.isSelected() ) {
        	elements_available.add(new ukim.finki.mps.delay_fucntion_generator.Element(ukim.finki.mps.delay_fucntion_generator.Element.REGISTER	,"H"));
        }
        if ( L.isSelected() ) {
        	elements_available.add(new ukim.finki.mps.delay_fucntion_generator.Element(ukim.finki.mps.delay_fucntion_generator.Element.REGISTER	,"L"));
        }
        if ( jRadioButton2.isSelected() ) {
        	//use it
        	elements_available.add(new ukim.finki.mps.delay_fucntion_generator.Element(ukim.finki.mps.delay_fucntion_generator.Element.STACK	,"valid stack"));
            
        }
        if ( jRadioButton3.isSelected() ) {
        	//use the commands
        	elements_available.add(new ukim.finki.mps.delay_fucntion_generator.Element(ukim.finki.mps.delay_fucntion_generator.Element.STACK	,"not valid stack"));   
        }
        if ( jRadioButton1.isSelected() ) {
        	//forget the stack
        }
     // Get number of items in the list
        int size = lstMemory.getModel().getSize(); // 4

        // Get all item objects
        for (int i=0; i<size; i++) {
            String location = lstMemory.getModel().getElementAt(i).toString();
        	elements_available.add(new ukim.finki.mps.delay_fucntion_generator.Element(ukim.finki.mps.delay_fucntion_generator.Element.MEMORY,location));   
            
        }
        ukim.finki.mps.delay_fucntion_generator.CodeBuilder c = new CodeBuilder();
        long plus_time = 0; 
        String plus_s = txtErrorPlus.getText();
        if ( plus_s.contains("%") ) {
        	double plus = Double.parseDouble(plus_s.replaceAll("%",""));
        	plus_time = (long) (plus*(double)time*0.01);            
        }
        else {
        	plus_time = Long.parseLong(plus_s);
        }
        long minus_time = 0; 
        String minus_s = txtErrorMinus.getText();
        if ( plus_s.contains("%") ) {
        	double minus = Double.parseDouble(minus_s.replaceAll("%",""));
        	minus_time = (long) (minus*(double)time*0.01);            
        }
        else {
        	minus_time = Long.parseLong(minus_s);
        }
        
        ukim.finki.mps.delay_fucntion_generator.DelayFunction d = c.buildDelayFunction(time-minus_time,time+minus_time, "8085", (ukim.finki.mps.delay_fucntion_generator.Element []) elements_available.toArray(new ukim.finki.mps.delay_fucntion_generator.Element[1]));
        System.out.println(d.time());
        if ( d == null ) {
        	
        }
        else {
        	txtCode.setText(d.toString());
        }
        
    }

    
    private ArrayList<String> list_data = new ArrayList<String>();
    private void jButton1ActionPerformed(ActionEvent evt) {
		//add button
    	String s = txtMemory.getText();
    	System.out.println(s);
    	if ( checkMemory(s) ) {
    		if ( !list_data.contains(s) ) {
	    		list_data.add( s );
				lstMemory.setListData( list_data.toArray() );
				lstMemory.repaint();
    		}
    	}	
	}
    
    private boolean checkMemory(String s) {
    	return true;/*
		try {
			int i = Integer.parseInt(s);
			if ( i >= 0 && i < 65536 ) return true;
		}
		catch ( Exception e ) {
			
		}
		return false;*/
	}

	private void jButton2ActionPerformed(ActionEvent evt) {
		//remove
		int idx = lstMemory.getSelectedIndex();
		if ( idx != -1 ) {
			list_data.remove(idx);
			lstMemory.setListData( list_data.toArray() );
			lstMemory.repaint();
    	}	
		
	}
    
    private void jButton3ActionPerformed(ActionEvent evt) {
		//clear
    	list_data = new ArrayList<String>();
    	lstMemory.setListData( list_data.toArray() );
		lstMemory.repaint();
	}
    
    // Variables declaration - do not modify
    private javax.swing.JCheckBox A;
    private javax.swing.JCheckBox B;
    private javax.swing.JCheckBox C;
    private javax.swing.JCheckBox D;
    private javax.swing.JCheckBox E;
    private javax.swing.JCheckBox H;
    private javax.swing.JCheckBox L;
    private javax.swing.ButtonGroup Stack;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList lstMemory;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTextArea txtCode;
    private javax.swing.JTextField txtDays;
    private javax.swing.JTextField txtErrorMinus;
    private javax.swing.JTextField txtErrorPlus;
    private javax.swing.JTextField txtHours;
    private javax.swing.JTextField txtMemory;
    private javax.swing.JTextArea txtMessages;
    private javax.swing.JTextField txtMins;
    private javax.swing.JTextField txtSecs;
    private javax.swing.JTextField txtStateDuration;
    private javax.swing.JTextField txtStates;
    private javax.swing.JTextField txtmSecs;
    private javax.swing.JTextField txtnSecs;
    // End of variables declaration

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;
}
