# SVM应用

## 概述

  SVM（Support Vector Machine，支持向量机）是机器学习算法的一种，属于有监督的机器学习。有监督的机器学习，其过程可以看做对一系列样本(x, y)，构建f(x) -> y的映射。该映射又称样本空间中的超平面，可用作分类（Classification）以及回归（Regression）两种用途。SVM的分类可分别适应二分类和多分类的情形。在海关的应用场景下，分类可对报关单、企业、商品等可定义类别属性的数据进行分类的预测，回归可用作商品价格等数值型数据的预测。

  本项目通过对台湾大学林智仁教授的 libsvm 库进行二次开发和封装，提供了更易于使用的算法接口。

## 数据集

  简单地说，有监督机器学习的样本数据从维度上可分为标签（label）和特征（feature）两部分。每一条样本数据应包含惟一的标签和若干特征值，其标签和特征均为数值型数据。例如，在分类的情形下，其标签可分别定义为-1（不涉案）和1（涉案）；特征可定义为数值化后的报关单数据项，例如-1（出口）和1（进口）、20.0（单价）。需要注意的是对于每一个特征，其最大值和最小值不宜相差过大，例如将日期拆分为年、月、日三个特征，以及使用单价和数量两个特征代替总价。

  同时，有监督机器学习的数据集又分为训练集和测试集两部分。算法通过对训练集的特征和标签进行学习，找到样本空间中的超平面（即特征到标签的映射），然后通过该超平面对测试集的标签进行预测，通过预测的准确率判断算法以及参数的好坏。可以看出，只有当特征与标签确实存在某种关系时，机器学习才能从中学习到对应的超平面。如果特征为路人甲每天抛硬币的结果（-1：反面，1：正面），标签为当日大盘的走势（-1：跌，1：涨），显然任何算法都难以从中找到映射关系。因此在特征选取时，应尽量选择与标签相关联的数据。

## 使用方法

  svm 算法的应用主要分为三个阶段，分别是数据准备、模型训练以及模型应用。

1. 数据准备阶段：

   数据准备阶段的主要工作是将数据转化成程序能够接受的形式，需要以下几步工作：

   * 将非数值型数据转化成数值型数据。例如对于进出口标识符（“I”或者“E”），程序能够自动将它们转变成数值。但是对于其它更复杂的非数值型数据，需要在训练前进行人工转化。例如使用港口代码而非港口名称，使用企业代码而非企业名称等。对于商品而言，税号并不能很好的起到代表作用，因此需要进行一定的工作。可参考的思路有：
     1. 建立转化字典，将相同的字符串映射成同一个数值。其思路其实是对字符串人工进行Hash，难度在于商品名称和商品描述的种类繁多且不规则，建立字典工作量巨大。
     2. 使用一些现有的 Hash 函数。但是最具代表性的 md5 算法得到的结果是128位16进制数，而大多数机器中整型数据占用64位，无法直接进行转换。
   * 将数据按照每行一个样本，形如 label feature1 feature2 ... 的方式存放在文件中。程序同样能够接受数据库中的数据，但是需要通过 query 去取数据，并不推荐。label 和 feature，feature 和 feature间的间隔符可以通过 SVMFileReader 类中的 setSeperator 方法指定。用例可见 svmDemos.setSeperater()。
   * 将数据读入Dataset类中。用例可见 svmDemos.readDataInFile()。
   * （可选）对数据集的每一列特征进行缩放，其目的是将特征缩放至相同的范围内（常见[0, 1]或[-1, 1]），以减轻数据倾斜造成的影响。缩放的原因可参考 [文献1](http://neerajkumar.org/writings/svm/) 以及 [文献2](http://www.csie.ntu.edu.tw/~cjlin/papers/guide/guide.pdf)。需要注意的是，并非缩放后的训练效果一定优于未缩放的效果，是否对数据进行缩放以及对数据进行何种缩放取决于数据的状况，推荐对数据集进行取样后分别测试其训练效果。由于缩放会使样本空间发生形变，因此对于训练集和测试集需要进行相同规模的缩放。关于训练集和测试集的更多信息见下一节模型训练，这里通过对缩放的参数进行保存来达到相同规模缩放的目的。缩放的方式有两种，分别是：
     1. 硬缩放，即将原数据线性缩放至一定区间内（常为[-1, 1]）。用例可见 svmDemos.linearScale。
     2. 软缩放，将每个特征减去该列的平均值，再处以标准差的两倍。

2. 模型训练阶段：

     模型训练阶段主要通过对参数进行调整，使得训练得到的模型在测试集上能取得最好的效果。

   **训练及测试**

     对于已有的数据集，我们通常将它分为两部分。一部分用于训练，另一部分假装不知道它们的标签，让算法根据训练得到的模型对它们的标签进行预测，再与它本身的标签进行对比，以评估模型的好坏。通常训练集的样本数量会大于等于测试集的样本数量。普通的训练用例可见 svmDemos.train()。

   **参数调整**

     训练阶段的主要任务是对模型的参数进行调整。主要调整的参数有以下几个：

   1. svm 类型。详见 svm_parameter.svm_type。其中的 C_SVC 和 NU_SVC 泛用于2分类和多分类的情形，通常使用 C_SVC；ONE_CLASS 用于训练集中仅有一类标签的情形，用法可见[知乎链接](https://www.zhihu.com/question/22365729)中的高票回答；EPSILON_SVR 和 NU_SVR 用于回归学习，通常选用 EPSILON_SVR。
   2. 核函数类型。详见 svm_parameter.kernel_type。通常在 LINEAR 和 RBF 中选择，且优先选择 RBF。RBF 理论上训练效果不会差于 LINEAR，但其训练复杂度更高，在面对海量数据时出于性能考虑需要选择 LINEAR。[文献2](https://link.zhihu.com/?target=https%3A//www.csie.ntu.edu.tw/~cjlin/papers/guide/guide.pdf)中对核函数进行了更详细的介绍。
   3. C，详见 svm_parameter.C。

   **Grid Search**

     然而，将数据集人工划分成训练集和测试集的方式存在较多不确定因素，可能会影响到训练结果。 例如数据集本身的分布就不均匀，或是难以确定训练集和测试集的界限。常用的做法是对整个数据集进行10折交叉验证。交叉验证的原理可参考 [文献3](http://www.jianshu.com/p/201a164e1b35)。简单地说就是将数据集每次划分成10份，取其1份做测试集，剩下的9份做训练集，反复训练10次，直至每个点都存在于测试集中过。

3. 模型应用阶段：



## 参数调优

