package com.example.healthyapp;

import android.util.Log;

public class Character {
    private String name;//이름
    private float height;//키
    private float weight;//몸무게
    private float calories;//칼로리
    private Level level;//레벨
    private int character;//캐릭터 이미지
    private int step;
    private float distance;

    public Character(String name, float height, float weight,int character,int level,float currentExp,float negativeExp, float calories,int step,float distance,long last_exercised)
    {
        this.name=name;
        this.height=height;
        this.weight=weight;
        this.character=character;
        this.level = new Level(level,currentExp,negativeExp,last_exercised);
        this.calories=calories;
        this.step=step;
        this.distance=distance;
    }

    public void CalorieAcquisition(float minute, float Mets)
    {
        //칼로리 계산
        calories+=minute*0.0175*Mets*weight;
        // duration * 0.0175 * Mets * weight(kg)
    }//계산

    public int getCharacter()
    {
        return character;
    }
    public void setCharacter(int character)
    {
        this.character=character;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public float getHeight() {
        return height;
    }
    public void setHeight(float height) {
        this.height = height;
    }

    public Level getLevel() {
        return level;
    }
    public void setLevel(Level level) {
        this.level = level;
    }

    public float getWeight() {
        return weight;
    }
    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public class Level {
        private int level;
        private float currentExperience;//현재경험치
        private float negativeExperience;//감소 경험치, 일정기간 이동하지 않다가 다시 이동하게 되면 증가됨.
        private long last_exercised; // 오랫동안 이동하지 않을 시 기록
        private int decrement_count; // 경고 알림, 일정 속도 이상의 이동 시에 초기화, 오랫동안 이동하지 않을 시 증가

        public Level(int level, float positiveExperience,float negativeExperience,long last_exercised) {
            this.level=level;
            this.currentExperience=positiveExperience;
            this.negativeExperience=negativeExperience;
            this.last_exercised=last_exercised;
            decrement_count=0;
        }

        public int getLevel(){
            return level;
        }
        public void setLevel(int level){
            this.level = level;
        }


        public float getCurrentExperience() {
            return currentExperience;
        }
        public void setCurrentExperience(float currentExperience)
        {
            this.currentExperience=currentExperience;
        }

        public long getLast_exercised() {
            return last_exercised;
        }
        public void setLast_exercised(long last_exercised)
        {
            this.last_exercised=last_exercised;
        }
        public void setLast_exercisedtoCurrent()
        {
            last_exercised=System.currentTimeMillis();
        }

        public void levelUp()
        {
            currentExperience-=getMaxExperience();
            level++;
        }

        public void negativeAcquisition(long last_exercised)
        {
            negativeExperience += (((float)(System.currentTimeMillis() - last_exercised) / (60* 60 * 24 * 3))/5);
            // 이틀간 1680kcal 이동치만큼을 이동한다고 가정(120*0.0175*5*80 * 2), 약 168 경험치를 얻게 됨.
            // 따라서 60 * 60 * 24 * 3으로 나누고 ms 단위 다시 그 값에 1000을 나눔.
            // 그리고 약 3개월에 이틀분의 치를 빼기 위해서 200을 곱함.(걷기만 하는 것이 아니라 뛰기도 하기 때문)
        }

        public void countDecrement()
        {
            decrement_count++;
        }
        public int getDecrement_count()
        {
            return decrement_count;
        }
        public void setDecrement_count(int decrement_count)
        {
            this.decrement_count = decrement_count;
        }

        public int getMaxExperience()
        {
            return (int)(Math.sqrt(level)*100);
        }

        public void expAcquisition(float exp)
        {
            //경험치 계산
            currentExperience += exp;
            // 성장도 =  칼로리 / 10 ( 성장 )
            // 비만도 =  negativeExperience ( 감소 )
            if(currentExperience < 0) currentExperience = 0;
        }//계산

        public float getNegativeExperience() {
            return negativeExperience;
        }
        public void setNegativeExperience(float negativeExperience)
        {
            this.negativeExperience=negativeExperience;
        }
    }
}