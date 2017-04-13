>GrennDao基本介绍
  ![greeDao.jpg](http://upload-images.jianshu.io/upload_images/2178834-5142c248b21bfb7b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
greenDAO is an object/relational mapping (ORM) tool for Android. It offers an object oriented interface to the relational database SQLite. ORM tools like greenDAO do many repetitive tasks for you and offer a simple interface to your data.
翻译：greendao是为Android提供的对象/关系映射（ORM）工具。它提供了一个面向对象的接口关系数据库SQLite。ORM工具greendao为你做重复的许多任务和为你的数据提供了一个简单的接口。

简单一句话：GreenDao就是简答化使用android的sqlite数据库。利用android自带的SQLiteOpenHelper来实现也是比较麻烦的，需要自己手写sql语句。具体使用可以参考[博客](https://github.com/Tikitoo/blog/issues/9)。通过使用GreenDao，可以大大简化数据库类的编写。接下来就让我们来学习怎么使用把。

#添加依赖
在你的project中```build.gradle```添加如下代码（添加了注释的部分）。
```
// In your root build.gradle file:
buildscript {
    repositories {
        jcenter()
        mavenCentral() // add repository
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:2.3.0'
        classpath 'org.greenrobot:greendao-gradle-plugin:3.2.2' // add plugin
    }
}
 
// In your app projects build.gradle file:
apply plugin: 'com.android.application'
apply plugin: 'org.greenrobot.greendao' // apply plugin
 
dependencies {
    compile 'org.greenrobot:greendao:3.2.2' // add library
}
```
在你要使用的要使用greenDao的module中添加如下的依赖：
```
apply plugin: 'com.android.application'
apply plugin:'org.greenrobot.greendao'//add

android {
    compileSdkVersion 25
    buildToolsVersion "25.0.2"
    defaultConfig {
        applicationId "reoger.hut.com.greendaostudent"
        minSdkVersion 16
        targetSdkVersion 25
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }


    greendao{
       schemaVersion 1
       daoPackage 'com.afa.tourism.greendao.gen' //设置grennDao生成代码的位置
       targetGenDir 'src/main/java'//默认目录
   }

}

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    androidTestCompile('com.android.support.test.espresso:espresso-core:2.2.2', {
        exclude group: 'com.android.support', module: 'support-annotations'
    })
    compile 'com.android.support:appcompat-v7:25.3.0'
    compile 'com.android.support.constraint:constraint-layout:1.0.0-beta3'
    testCompile 'junit:junit:4.12'

    compile 'org.greenrobot:greendao:3.2.2'//添加依赖

}

```
上面添加了注释的地方就是要注意添加代码的地方。
至此，我们就添加了对GreenDao的依赖，而且GrennDao本身也不算大，打包出来100k的样子，不会过大增添apk的文件大小。

# 开始使用
## 1.新建一个Bean对象User，代码如下：
```
public class User {

    @Id
    private Long id;

   // @NotNull
    private String text;
    private String comment;
    private java.util.Date date;
}
```
很简单吧，然后```Make Project```(快捷键```ctrl+F9```)。GrennDao就会自动帮我们生成如下的代码：
![Paste_Image.png](http://upload-images.jianshu.io/upload_images/2178834-82be9b032dd11be2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
这个代码生成的位置是前面我们添加依赖的时```daoPackage ```指定的。

##2. 初始化DaoSession
一般来说，初始化DaoSession的工作我们会放在Application中进行。自定义MyApplication来进行初始化工作，代码如下：
```
public class MyApplication extends Application {
    public static final boolean ENCRYPTED = true;

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? "notes-db-encrypted" : "notes-db");
        Database db = !ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
```
Tips：不要忘记在manifests中注册application哦。
## 3. 进行数据库的增删改查工作
通过前面的准备工作，我们现在可以很方便的进行数据库的增删改查工作了。接下来通过代码来进行说明GreenDao怎么来进行数据库的增删改查操作。
```
package reoger.hut.com.greendaostudent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afa.tourism.greendao.gen.DaoSession;
import com.afa.tourism.greendao.gen.UserDao;

import org.greenrobot.greendao.query.Query;

import java.util.Date;
import java.util.List;

import reoger.hut.com.greendaostudent.bean.User;

/**
 * 此项目用于学习GreenDao的基本使用
 * greenDao的官方地址http://greenrobot.org/greendao/
 * github地址https://github.com/greenrobot/greenDAO
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private UserDao userDao;
    private AutoCompleteTextView text;
    private AutoCompleteTextView comment;
    private Button butAddData;
    private Button butShowData;
    private Button butDelData;
    private Button butModifyData;
    private TextView tShow;
    private EditText num;
    private Query<User> userQuery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DaoSession daoSession = ((MyApplication) getApplication()).getDaoSession();
        userDao = daoSession.getUserDao();
        initView();
    }

    private void initView() {
        text = (AutoCompleteTextView) findViewById(R.id.edit_text);
        comment = (AutoCompleteTextView) findViewById(R.id.edit_comment);
        tShow = (TextView) findViewById(R.id.text_show_data);
        num = (EditText) findViewById(R.id.exit_id);
        butAddData = (Button) findViewById(R.id.but_add_data);
        butShowData  = (Button) findViewById(R.id.but_show_data);
        butDelData = (Button) findViewById(R.id.but_del_data);
        butModifyData = (Button) findViewById(R.id.but_modify_data);

        butAddData.setOnClickListener(this);
        butShowData.setOnClickListener(this);
        butDelData.setOnClickListener(this);
        butModifyData.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.but_add_data://增
                String test = text.getText().toString();
                String con = comment.getText().toString();
                User user = new User();
                user.setComment(con);
                user.setDate(new Date());
                user.setText(test);
                userDao.insert(user);
                text.setText("");
                comment.setText("");
                break;
            case R.id.but_show_data://查询
                //按找id z-a排序查询
                // userQuery = userDao.queryBuilder().orderDesc(UserDao.Properties.Id).build();
                //按找id a-a排序查询
                userQuery = userDao.queryBuilder().orderAsc(UserDao.Properties.Id).build();
                //查询满足指定属性值的结果
                //userQuery = userDao.queryBuilder().where(UserDao.Properties.Text.eq(33),UserDao.Properties.Comment.eq(22)).build();
                List<User> datalist= userQuery.list();
                StringBuffer res = new StringBuffer();
                for (User user1 : datalist) {
                    res.append("id= "+user1.getId());
                    res.append("  text="+user1.getText());
                    res.append("  comment="+user1.getComment()+"\n");
                    //res.append(" date="+user1.getDate()+"\n");
                }
                tShow.setText(res.toString());
                break;
            case R.id.but_del_data://删除
                String index = num.getText().toString();
                userDao.deleteByKey(Long.valueOf(index));
                num.setText("");
                break;
            case R.id.but_modify_data://修改
                String www = num.getText().toString();
                String test2 = text.getText().toString();
                String con2 = comment.getText().toString();
                User user2 = new User();
                user2.setId(Long.valueOf(www));
                user2.setComment(con2);
                user2.setDate(new Date());
                user2.setText(test2);
                userDao.update(user2);

                text.setText("");
                comment.setText("");
                num.setText("");
                break;
        }
    }
}
```
通过上面的代码就实现了数据库的CRUD操作，是不是很简单~。对了，顺便贴上xml的布局文件代码：
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
	xmlns:android="http://schemas.android.com/apk/res/android"
	xmlns:tools="http://schemas.android.com/tools"
	android:layout_width="match_parent"
	android:layout_height="match_parent"
	android:orientation="vertical"
	tools:context="reoger.hut.com.greendaostudent.MainActivity">

	<LinearLayout
		android:layout_width="match_parent"
		android:layout_height="wrap_content"
		android:orientation="horizontal">
		
	 <AutoCompleteTextView
		 android:id="@+id/edit_comment"
		 android:layout_width="0dp"
		 android:hint="comment"
		 android:layout_height="wrap_content"
		 android:layout_weight="1"
		 android:padding="5dp" />
	
	<AutoCompleteTextView
		android:id="@+id/edit_text"
		android:layout_width="0dp"
		android:layout_height="wrap_content"
		android:hint="text"
		android:layout_weight="1"
	    android:padding="5dp"
		/>
	</LinearLayout>
   
	<Button
		android:id="@+id/but_add_data"
		android:layout_width="match_parent"
		android:layout_height="wrap_content"
		android:text="添加数据"
	    android:padding="5dp"
	 />
	<ImageView
		android:layout_width="match_parent"
		android:layout_height="3dp"
		android:background="#ff0"/>
		<Button
			android:layout_width="match_parent"
			android:layout_height="wrap_content"
			android:text="显示数据"
			android:id="@+id/but_show_data"/>
	<TextView
		android:layout_width="match_parent"
		android:layout_height="wrap_content"
		android:id="@+id/text_show_data"
		android:layout_gravity="center"
		android:gravity="center"
		android:hint="这里将显示数据库中的数据"/>
	
	<Button
		android:layout_width="match_parent"
		android:layout_height="wrap_content"
		android:id="@+id/but_del_data"
		android:text="删除"/>
	
	<EditText
		android:layout_width="match_parent"
		android:layout_height="40dp"
		android:hint="输入要删除或者要修改的id值"
		android:id="@+id/exit_id"
		android:inputType="number"
		/>
	<Button
		android:layout_width="match_parent"
		android:layout_height="wrap_content"
		android:id="@+id/but_modify_data"
		android:text="修改"/>
</LinearLayout>
```
最终达到的效果是这个样子的：

![gif1.gif](http://upload-images.jianshu.io/upload_images/2178834-1507e928da44428a.gif?imageMogr2/auto-orient/strip)

上面的操作基本满足了一般需求。再记录几个比较常见的注释：
<strong> 
* @Entity 可以指定表明，形如```@Entity(nameInDb = "tb_user") ```默认表名为类名
* @Id 表明主键，可以指定自增，形如``` @Id(autoincrement = true) ```
* @NotNull 非空
* @Unique 添加唯一约束
* @Keep  在下一次运行产生dao代码期间，被该注解标记的，保持不变
* @Generated model发生改变，在下一次运行产生dao代码期间，被该注解标记的，可以改变或移除
---
* @OrderBy:指定排序
* @ToOne 是将自己的一个属性与另一个表建立关联（外键） 
* @ToMany的属性referencedJoinProperty，类似于外键约束。
* @Property 用于设置属性在数据库中的列名（默认不写就是保持一致）
* @Transient 标识这个字段是自定义的不会创建到数据库表里
* @JoinProperty 对于更复杂的关系，可以使用这个注解标明目标属性的源属性。
* @JoinEntity 在做NM多对多映射的时候使用
</strong>
