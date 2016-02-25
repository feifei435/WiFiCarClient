# WiFiCarClient
本人2014年本科毕业设计，基于OpenWrt的视频监控WiFi遥控车的Android客户端源码

主要功能：</br>
1. 通过socket连接小车上的ser2net进而向串口发送信息控制小车和摄像头云台的运动方向</br>
2. 从小车上获取实时mjpeg视频流，并可以对视频截图，录像

WiFiCarClient各线程通信同步图
![image](https://github.com/feifei435/WiFiCarClient/raw/master/diagrams/WiFiCarClient%E5%90%84%E7%BA%BF%E7%A8%8B%E9%80%9A%E4%BF%A1%E5%90%8C%E6%AD%A5%E5%9B%BE.png)
Android心跳包消息
![image](https://github.com/feifei435/WiFiCarClient/raw/master/diagrams/Android%E5%BF%83%E8%B7%B3%E5%8C%85%E6%B6%88%E6%81%AF.png)
Android端WiFi初始化流程
![image](https://github.com/feifei435/WiFiCarClient/raw/master/diagrams/Android%E7%AB%AFinitWiFiConnection.png)

运行截图：
![image](https://github.com/feifei435/WiFiCarClient/raw/master/screenshot/start.png)

![image](https://github.com/feifei435/WiFiCarClient/raw/master/screenshot/main_interface.png)

![image](https://github.com/feifei435/WiFiCarClient/raw/master/screenshot/setting_interface.png)

WiFiCar实物
![image](https://github.com/feifei435/WiFiCarClient/raw/master/screenshot/WiFiCar实物1.png)
