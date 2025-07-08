-- 清空测试数据（如果需要）
DELETE FROM mall_specification_option WHERE product_id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
DELETE FROM mall_product_specification WHERE product_id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
DELETE FROM mall_product WHERE id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
DELETE FROM mall_category WHERE id IN (100001, 100002, 100003, 100004);

-- 插入商品分类数据
INSERT INTO mall_category (id, name, status, sort_order, create_time, update_time) VALUES 
(100001, '文学', 1, 1, NOW(), NOW()),
(100002, '自然科学', 1, 2, NOW(), NOW()),
(100003, '经济管理', 1, 3, NOW(), NOW()),
(100004, '教育考试', 1, 4, NOW(), NOW());

-- 插入商品数据
INSERT INTO mall_product (id, category_id, category_name, name, subtitle, main_image, sub_images, detail, price, stock, sales, status, create_time, update_time) VALUES
-- 文学类
(1, 100001, '文学', '《红楼梦》', '中国古典文学四大名著之一', 'https://ts1.tc.mm.bing.net/th/id/R-C.0567ec2a9039b371c2b4e8a83b4bb0f4?rik=229rW9nSPWGMAQ&riu=http%3a%2f%2fwww.guoxuemeng.com%2ffiles%2fimg%2f1912%2f1JZ031N-0.jpg&ehk=4Vg%2fhM4XsXEHl%2bFZSQQC2S2dFkntcAkYePqS7P9JfFM%3d&risl=&pid=ImgRaw&r=0', 'https://tse4-mm.cn.bing.net/th/id/OIP-C.Kzjmc3iZwZz3El3bGE8JtwHaKk?cb=iwp2&rs=1&pid=ImgDetMain', '<p>《红楼梦》是中国古典文学的巅峰之作，描绘了贾宝玉、林黛玉、薛宝钗等人的爱情悲剧和贾府的兴衰历程。</p>', 45.80, 200, 328, 1, NOW(), NOW()),
(2, 100001, '文学', '《百年孤独》', '魔幻现实主义文学经典', 'https://img.alicdn.com/i3/101450072/O1CN01JXSrRS1CP1HcgQpqL-101450072.jpg', 'https://img.alicdn.com/bao/uploaded/i3/101450072/O1CN01C1eErb1CP1Lzf5ppI_!!0-item_pic.jpg', '<p>加西亚·马尔克斯的代表作，讲述了布恩迪亚家族七代人的传奇故事，展现了拉丁美洲的历史变迁。</p>', 39.80, 150, 195, 1, NOW(), NOW()),
(3, 100001, '文学', '《活着》', '余华经典代表作', 'https://img.alicdn.com/bao/uploaded/i4/1049653664/O1CN01sxLzx91cwA3MjuUFa_!!0-item_pic.jpg', 'https://uimgproxy.suning.cn/uimg1/sop/commodity/ZER9ADzmI7fBcKJCkq2BlQ.jpg', '<p>余华的长篇小说代表作，以朴实的语言描述了一个普通农民福贵的人生遭遇，展现了人性的坚韧与生命的尊严。</p>', 28.00, 180, 278, 1, NOW(), NOW()),

-- 自然科学类
(4, 100002, '自然科学', '《时间简史》', '史蒂芬·霍金科普经典', 'https://img.alicdn.com/bao/uploaded/i4/1098343833/TB10lbxf9CWBuNjy0FhXXb6EVXa_!!0-item_pic.jpg', null, '<p>史蒂芬·霍金的科普杰作，用通俗易懂的语言解释了宇宙学的基本概念，探讨了时间、空间和宇宙的奥秘。</p>', 42.00, 120, 156, 1, NOW(), NOW()),
(5, 100002, '自然科学', '《物种起源》', '达尔文进化论经典著作', 'https://img.alicdn.com/bao/uploaded/i4/2263306098/TB1rbwMdKOSBuNjy0FdXXbDnVXa_!!0-item_pic.jpg', null, '<p>达尔文的《物种起源》奠定了进化生物学的基础，提出了自然选择理论，是现代生物学的里程碑式著作。</p>', 35.60, 90, 88, 1, NOW(), NOW()),

-- 经济管理类
(6, 100003, '经济管理', '《经济学原理》', '曼昆经济学教材', 'https://m.360buyimg.com/babel/jfs/t1/114022/9/9487/819189/5eda0447Ed5cce2ef/dc08b675667de462.jpg', 'https://ts1.tc.mm.bing.net/th/id/R-C.e9684991403e4ceae3be6b5d8ff3a04f?rik=JyVbUi2SPLbhwg&riu=http%3a%2f%2fwww.tup.tsinghua.edu.cn%2fupload%2fbigbookimg%2f064908-01.jpg&ehk=eabfRd1eSfRcwGBicIHq3L16ubvxLOvpLCHR6%2f%2bDyZs%3d&risl=&pid=ImgRaw&r=0,https://tse2-mm.cn.bing.net/th/id/OIP-C.ejGeVHCJprrb83hV_X23jwHaKg?cb=iwp2&rs=1&pid=ImgDetMain', '<p>格雷戈里·曼昆编写的经济学入门教材，内容全面，逻辑清晰，是学习经济学的经典教材。</p>', 89.00, 100, 145, 1, NOW(), NOW()),
(7, 100003, '经济管理', '《管理学》', '现代管理学理论与实践', 'https://ts1.tc.mm.bing.net/th/id/R-C.9758db72c9ff733541bcbaf2f98d399f?rik=m4d7QrEuxLnMqw&riu=http%3a%2f%2fnetshopimg.oss-cn-beijing.aliyuncs.com%2fimages%2f0%2f2020%2f09%2f29%2fgoods_img%2fi35d1d830ceb1448ff3150fead3b1e71d.jpg&ehk=uZqrQmj0EAPNWwHZixlAwfyCMvifJ%2b4Tg5ZZogJzx9Q%3d&risl=&pid=ImgRaw&r=0', 'https://ts1.tc.mm.bing.net/th/id/R-C.77c30e1eddab5161440128af1d347013?rik=%2bRlv8ydgbURHgA&riu=http%3a%2f%2fwww.tup.tsinghua.edu.cn%2fupload%2fbigbookimg3%2f091343-01.jpg&ehk=iJ7%2brIXtleemHmPzVImKomv96aGOb5Cwt6aH0Pxn2DA%3d&risl=&pid=ImgRaw&r=0', '<p>系统介绍现代管理学的基本理论、方法和技能，适合管理专业学生和管理实践者学习使用。</p>', 68.00, 80, 134, 1, NOW(), NOW()),

-- 教育考试类
(8, 100004, '教育考试', '《高等数学》', '大学数学基础教材', 'https://img.alicdn.com/bao/uploaded/O1CN01iXkaLw1OMyuDYzrZp_!!6000000001692-0-yinhe.jpg', null, '<p>高等教育出版社出版的高等数学教材，内容包括极限、导数、积分等基础数学知识，是理工科学生必备教材。</p>', 56.00, 300, 527, 1, NOW(), NOW());

-- 添加一个下架状态的商品用于测试
INSERT INTO mall_product (id, category_id, category_name, name, subtitle, main_image, detail, price, stock, sales, status, create_time, update_time) VALUES
(9, 100001, '文学', '《已下架小说》', '这是一本已下架的图书', 'http://example.com/images/9.jpg', '<p>这本图书已经下架</p>', 29.90, 0, 0, 2, NOW(), NOW());

-- 添加一个已删除状态的商品用于测试
INSERT INTO mall_product (id, category_id, category_name, name, subtitle, main_image, detail, price, stock, sales, status, create_time, update_time) VALUES
(10, 100001, '文学', '《已删除图书》', '这是一本已删除的图书', 'http://example.com/images/10.jpg', '<p>这本图书已经删除</p>', 39.90, 0, 0, 3, NOW(), NOW()); 


-- 先清空测试数据
DELETE FROM mall_specification_option WHERE product_id > 100000;
DELETE FROM mall_product_specification WHERE product_id > 100000;
DELETE FROM mall_product WHERE id > 100000;
DELETE FROM mall_category WHERE id > 100000;

-- 插入商品分类
INSERT INTO mall_category (id, name, status, sort_order, create_time, update_time) VALUES
(100001, '文学', 1, 1, NOW(), NOW()),
(100002, '自然科学', 1, 2, NOW(), NOW()),
(100003, '经济管理', 1, 3, NOW(), NOW()),
(100004, '教育考试', 1, 4, NOW(), NOW());

-- 插入商品 - 不带规格
INSERT INTO mall_product (id, category_id, category_name, name, subtitle, main_image, sub_images, detail, price, stock, sales, status, has_specification, create_time, update_time) VALUES
(100001, 100001, '文学', '《三体》', '刘慈欣科幻小说三部曲', 'https://netshopimg.oss-cn-beijing.aliyuncs.com/images/22010601061822/description/O1CN01509XxL1RXuw0oKsSD_!!2579222122.png', 'https://img.alicdn.com/bao/uploaded/i1/2782902788/O1CN01l3Wocz1WSx2BzbZAP_!!0-item_pic.jpg,https://tse4-mm.cn.bing.net/th/id/OIP-C.euMYRD2GDWJs40zJIYAT_wHaHa?cb=iwp2&rs=1&pid=ImgDetMain', '<p>刘慈欣创作的长篇科幻小说，讲述了地球人类文明和三体文明的信息交流、生死搏杀及两个文明在宇宙中的兴衰历程。</p>', 79.80, 150, 228, 1, 0, NOW(), NOW()),
(100002, 100001, '文学', '《平凡的世界》', '路遥茅盾文学奖获奖作品', 'https://img.alicdn.com/bao/uploaded/i3/2041592426/O1CN01gtZMep1Tn9do7ht8R_!!0-item_pic.jpg', 'https://img.alicdn.com/i2/1084487551/O1CN0145VfEP25ePWN9RpWK_!!1084487551.jpg,https://img.alicdn.com/i1/2041592426/O1CN01wRxvhq1Tn9Xw6jD2k_!!2041592426.jpg', '<p>路遥创作的百万字长篇小说，全景式地表现了中国当代城乡社会生活，获得第三届茅盾文学奖。</p>', 68.00, 120, 195, 1, 0, NOW(), NOW()),
(100003, 100002, '自然科学', '《从一到无穷大》', '科学中的事实和臆测', 'https://img.alicdn.com/i1/2200779298895/O1CN01lYn7U22FZxkvI3FKE_!!2200779298895.jpg', 'https://image12.bookschina.com/2023/20230118/2/8978589.jpg', '<p>乔治·伽莫夫的科普经典，用生动有趣的语言介绍了20世纪以来科学中的一些重大进展。</p>', 42.00, 100, 156, 1, 0, NOW(), NOW()),
(100004, 100004, '教育考试', '《大学英语四级真题》', '历年真题详解', 'https://img.alicdn.com/i1/1973500330/O1CN01j1jXTK1EJBVMyNrAj_!!1973500330.jpg', 'https://tse1-mm.cn.bing.net/th/id/OIP-C.t45-R6JUqVvxrdDEsJTWlgHaHa?cb=iwp2&rs=1&pid=ImgDetMain', '<p>收录了近年来大学英语四级考试真题，配有详细解析，帮助考生了解考试规律，提高应试能力。</p>', 35.80, 200, 289, 1, 0, NOW(), NOW()),
(100005, 100002, '自然科学', '《生命是什么》', '薛定谔科学哲学经典', 'https://lib.zjsru.edu.cn/__local/D/2A/B2/5B1DAFA00118FB84FC0F633C52F_D1CCCA6B_8A54A.png', 'https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/27064/12/481/213882/5c0a3559E35a5b94f/e48abca1254dacac.jpg!q80.dpg', '<p>薛定谔从物理学角度探讨生命现象的经典著作，对分子生物学的发展产生了重要影响。</p>', 38.00, 0, 168, 1, 0, NOW(), NOW()); -- 设置无库存

-- 插入商品 - 带规格
-- 英语教材 - 有版本和装帧规格
INSERT INTO mall_product (id, category_id, category_name, name, subtitle, main_image, sub_images, detail, price, stock, sales, status, has_specification, create_time, update_time) VALUES
(100006, 100004, '教育考试', '《新概念英语》', '经典英语学习教材', 'https://img.alicdn.com/bao/uploaded/i4/2129856610/O1CN011yhQSoFKR8Umol1_!!0-item_pic.jpg', 'https://img.alicdn.com/bao/uploaded/i1/2415011287/O1CN01k4vrhE1LNUJbV3d38_!!0-item_pic.jpg,https://m.360buyimg.com/mobilecms/s750x750_jfs/t4363/347/3081346804/578276/6b6acb46/58db3879N9f58aece.jpg!q80.dpg', '<p>朗文出版的经典英语学习教材，分为四册，从基础到高级，适合各个水平的英语学习者。</p>', 45.00, 0, 187, 1, 1, NOW(), NOW());

-- 已删除的商品
INSERT INTO mall_product (id, category_id, category_name, name, subtitle, main_image, sub_images, detail, price, stock, sales, status, has_specification, create_time, update_time) VALUES
(100008, 100001, '文学', '《已绝版文学作品》', '绝版经典文学', 'images/book8.jpg', 'images/book8_1.jpg', '<p>已经绝版的经典文学作品</p>', 88.00, 5, 203, 3, 0, NOW(), NOW());

-- 插入规格选项 - 新概念英语
INSERT INTO mall_specification_option (product_id, spec_key, spec_values, create_time, update_time) VALUES
(100006, 'volume', '["第一册","第二册","第三册","第四册"]', NOW(), NOW()),
(100006, 'binding', '["平装","精装","电子版"]', NOW(), NOW());

-- 插入规格选项 - 现代管理学原理
INSERT INTO mall_specification_option (product_id, spec_key, spec_values, create_time, update_time) VALUES
(100007, 'edition', '["第5版","第6版","第7版"]', NOW(), NOW()),
(100007, 'binding', '["平装","精装","电子版"]', NOW(), NOW());

-- 插入规格组合 - 新概念英语
INSERT INTO mall_product_specification (product_id, specifications, price_adjustment, stock, sales, status, create_time, update_time) VALUES
(100006, '{"volume":"第一册","binding":"平装"}', 0.00, 50, 25, 1, NOW(), NOW()),
(100006, '{"volume":"第一册","binding":"精装"}', 15.00, 30, 12, 1, NOW(), NOW()),
(100006, '{"volume":"第一册","binding":"电子版"}', -10.00, 999, 45, 1, NOW(), NOW()),
(100006, '{"volume":"第二册","binding":"平装"}', 0.00, 45, 20, 1, NOW(), NOW()),
(100006, '{"volume":"第二册","binding":"精装"}', 15.00, 25, 8, 1, NOW(), NOW()),
(100006, '{"volume":"第二册","binding":"电子版"}', -10.00, 999, 38, 1, NOW(), NOW()),
(100006, '{"volume":"第三册","binding":"平装"}', 5.00, 40, 15, 1, NOW(), NOW()),
(100006, '{"volume":"第三册","binding":"精装"}', 20.00, 20, 6, 1, NOW(), NOW()),
(100006, '{"volume":"第三册","binding":"电子版"}', -5.00, 999, 25, 1, NOW(), NOW()),
(100006, '{"volume":"第四册","binding":"平装"}', 10.00, 35, 12, 1, NOW(), NOW()),
(100006, '{"volume":"第四册","binding":"精装"}', 25.00, 15, 4, 1, NOW(), NOW()),
(100006, '{"volume":"第四册","binding":"电子版"}', 0.00, 999, 18, 1, NOW(), NOW());

-- 插入规格组合 - 现代管理学原理
INSERT INTO mall_product_specification (product_id, specifications, price_adjustment, stock, sales, status, create_time, update_time) VALUES
(100007, '{"edition":"第5版","binding":"平装"}', -20.00, 20, 15, 1, NOW(), NOW()), -- 第5版便宜20元
(100007, '{"edition":"第5版","binding":"精装"}', -5.00, 15, 8, 1, NOW(), NOW()),
(100007, '{"edition":"第5版","binding":"电子版"}', -30.00, 999, 25, 1, NOW(), NOW()),
(100007, '{"edition":"第6版","binding":"平装"}', 0.00, 35, 20, 1, NOW(), NOW()),
(100007, '{"edition":"第6版","binding":"精装"}', 15.00, 25, 12, 1, NOW(), NOW()),
(100007, '{"edition":"第6版","binding":"电子版"}', -15.00, 999, 35, 1, NOW(), NOW()),
(100007, '{"edition":"第7版","binding":"平装"}', 20.00, 40, 25, 1, NOW(), NOW()), -- 第7版最新版加价20元
(100007, '{"edition":"第7版","binding":"精装"}', 35.00, 30, 18, 1, NOW(), NOW()),
(100007, '{"edition":"第7版","binding":"电子版"}', 5.00, 0, 28, 0, NOW(), NOW()); -- 第7版电子版已售罄 