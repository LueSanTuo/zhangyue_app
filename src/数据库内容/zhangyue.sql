/*
Navicat MySQL Data Transfer

Source Server         : homework
Source Server Version : 80013
Source Host           : localhost:3306
Source Database       : zhangyue

Target Server Type    : MYSQL
Target Server Version : 80013
File Encoding         : 65001

Date: 2018-12-27 11:07:45
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for book
-- ----------------------------
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book` (
  `id` char(18) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `author` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of book
-- ----------------------------
INSERT INTO `book` VALUES ('1', '浮图塔', '尤四姐');
INSERT INTO `book` VALUES ('10', '农女学习手册', '匿名');
INSERT INTO `book` VALUES ('11', '穿越古代嫁了个痞子', '匿名');
INSERT INTO `book` VALUES ('12', '穿越之农门骄女', '匿名');
INSERT INTO `book` VALUES ('13', '极品婆婆', '匿名');
INSERT INTO `book` VALUES ('14', '大姐当家', '匿名');
INSERT INTO `book` VALUES ('15', '小寡妇招夫记', '匿名');
INSERT INTO `book` VALUES ('16', '农媳当家', '匿名');
INSERT INTO `book` VALUES ('17', '战死的相公回来了', '匿名');
INSERT INTO `book` VALUES ('18', '青山深处有人家', '匿名');
INSERT INTO `book` VALUES ('19', '木匠家的小娘子', '匿名');
INSERT INTO `book` VALUES ('2', '驸马守则', '清歌一片');
INSERT INTO `book` VALUES ('20', '娶妻重生', '匿名');
INSERT INTO `book` VALUES ('21', '京城头号绯闻', '匿名');
INSERT INTO `book` VALUES ('3', '欢喜债', '笑佳人');
INSERT INTO `book` VALUES ('4', '掌柜攻略', '笑佳人');
INSERT INTO `book` VALUES ('5', '丑橘', '匿名');
INSERT INTO `book` VALUES ('6', '带上将军好种田', '匿名');
INSERT INTO `book` VALUES ('7', '山民锦绣', '匿名');
INSERT INTO `book` VALUES ('8', '蔬香门第', '匿名');
INSERT INTO `book` VALUES ('9', '棠锦', '匿名');

-- ----------------------------
-- Table structure for book_marks
-- ----------------------------
DROP TABLE IF EXISTS `book_marks`;
CREATE TABLE `book_marks` (
  `account` varchar(18) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `bookId` char(18) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `chapterId` int(11) NOT NULL,
  `firstLine` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `process` varchar(5) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '0.00',
  `date` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`account`,`bookId`,`chapterId`,`process`),
  KEY `FK_b_bm_id` (`bookId`),
  CONSTRAINT `FK_b_bm_id` FOREIGN KEY (`bookId`) REFERENCES `book` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_u_bm_account` FOREIGN KEY (`account`) REFERENCES `user` (`account`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of book_marks
-- ----------------------------
INSERT INTO `book_marks` VALUES ('1120254031', '1', '1', '　　人常说一朝天子一朝臣，后宫的女人何', '0.11', '2018/12/2115:18:37');
INSERT INTO `book_marks` VALUES ('1120254031', '1', '1', '刺花，她也是笑着的。李美人没她那么好的', '0.21', '2018/12/2115:24:13');
INSERT INTO `book_marks` VALUES ('1120254031', '14', '1', '	1.第一章 窦花', '0.00', '2018/12/22 11:13:16');
INSERT INTO `book_marks` VALUES ('utestbaby', '1', '1', '1、惊塞雁', '0.00', '2018/12/2711:01:07');
INSERT INTO `book_marks` VALUES ('utestbaby', '1', '1', '她们同批进宫，譬如乡里赴考的生员，要是', '0.32', '2018/12/27 11:04:51');
INSERT INTO `book_marks` VALUES ('utestbaby', '1', '1', '得？眼下皇上病势汹汹，有门道的早就活动', '0.63', '2018/12/27 11:04:56');
INSERT INTO `book_marks` VALUES ('utestbaby', '1', '2', '	2、春欲暮', '0.00', '2018/12/27 11:05:01');
INSERT INTO `book_marks` VALUES ('utestbaby', '1', '2', '是储君夺臣妻，传出去岂是好听的？这事儿', '0.36', '2018/12/27 11:05:11');
INSERT INTO `book_marks` VALUES ('utestbaby', '1', '3', '面上桃色如春，呓语似的呢喃，“我知道你不', '0.22', '2018/12/27 11:05:16');
INSERT INTO `book_marks` VALUES ('utestbaby', '1', '3', '　　他这份小心，倒叫几个秉笔、随堂心头', '0.43', '2018/12/27 11:05:20');
INSERT INTO `book_marks` VALUES ('utestbaby', '1', '4', '，又觉得像是坠进了噩梦，怎么都醒不过来', '0.13', '2018/12/27 11:05:24');
INSERT INTO `book_marks` VALUES ('utestbaby', '14', '1', '	1.第一章 窦花', '0.00', '2018/12/27 11:06:40');
INSERT INTO `book_marks` VALUES ('utestbaby', '14', '3', '	3.第三章 赶集', '0.00', '2018/12/27 11:06:51');
INSERT INTO `book_marks` VALUES ('utestbaby', '14', '3', '　　“大姐，你看那是不是一家毛皮店？”窦青', '0.24', '2018/12/27 11:06:55');
INSERT INTO `book_marks` VALUES ('utestbaby', '16', '1', '明明在酒窖里挨个测量酒缸温度，准备起花', '0.12', '2018/12/27 11:06:04');
INSERT INTO `book_marks` VALUES ('utestbaby', '16', '1', '里也是明白事儿的。他哪能不清楚，在战场', '0.38', '2018/12/27 11:06:07');
INSERT INTO `book_marks` VALUES ('utestbaby', '16', '2', '	2.农家和乐', '0.00', '2018/12/27 11:06:20');
INSERT INTO `book_marks` VALUES ('utestbaby', '16', '2', '　　“娘，我的身子也大好了，你也不用总看', '0.26', '2018/12/27 11:06:24');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `account` varchar(18) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `password` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT '',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT '',
  `exp` int(11) DEFAULT '0',
  PRIMARY KEY (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1120254031', 'zxcvbnm', '哈哈', '0');
INSERT INTO `user` VALUES ('utestbaby', 'qweqwe', '测试员', '1000');

-- ----------------------------
-- Table structure for user_read_record
-- ----------------------------
DROP TABLE IF EXISTS `user_read_record`;
CREATE TABLE `user_read_record` (
  `account` varchar(255) COLLATE utf8_bin NOT NULL,
  `bookId` varchar(255) COLLATE utf8_bin NOT NULL,
  `chapterId` int(11) NOT NULL,
  `process` float(5,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`account`,`bookId`),
  KEY `FK_b_urr_id` (`bookId`),
  CONSTRAINT `FK_b_urr_id` FOREIGN KEY (`bookId`) REFERENCES `book` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_u_urr_account` FOREIGN KEY (`account`) REFERENCES `user` (`account`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of user_read_record
-- ----------------------------
INSERT INTO `user_read_record` VALUES ('1120254031', '1', '1', '0.00');
INSERT INTO `user_read_record` VALUES ('1120254031', '10', '1', '0.00');
INSERT INTO `user_read_record` VALUES ('1120254031', '11', '1', '0.00');
INSERT INTO `user_read_record` VALUES ('1120254031', '12', '1', '0.00');
INSERT INTO `user_read_record` VALUES ('1120254031', '13', '1', '0.00');
INSERT INTO `user_read_record` VALUES ('1120254031', '2', '1', '0.00');
INSERT INTO `user_read_record` VALUES ('1120254031', '4', '1', '0.00');
