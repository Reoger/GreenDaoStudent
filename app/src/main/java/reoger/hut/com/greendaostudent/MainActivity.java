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
