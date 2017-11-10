# ListViewDelete
基于ListView和ArrayAdapter,实现长按，可多选子项并删除，考虑了ListView效率提升问题。

效果如下：<br>
![图片加载失败](https://github.com/HeTingwei/ListViewDelete/blob/master/doc/%E6%95%88%E6%9E%9C.gif)<br>
心得和总结：<br>
ArrayAdapter的notifyDataSetInvalidated,在代码中，可能不能顺序执行，其后的代码可能在其完成刷新前，就执行了。<br>
如果CheckBox是响应父控件的,而：visibility和GON的话，在GONE是改变选中状态，会在变成Visibility时显示一次选中状态发生改变的时候的动画。


