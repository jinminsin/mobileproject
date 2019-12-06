package com.example.healthyapp;

public class Character {
    private String name;//이름
    private float height;//키
    private float weight;//몸무게
    private int calories;//칼로리
    private int sleep_time;//수면 시간
    private int wake_time;//기상 시간
    private Level level;//레벨
    private int character;//캐릭터 이미지

    public static int getHours(int time) { return time / 60; }
    public static int getMinutes(int time) { return time % 60; }

    public Character(String name, float height, float weight,int character,int level,int experience,int calories)
    {
        this.name=name;
        this.height=height;
        this.weight=weight;
        this.character=character;
        sleep_time=-1;//default
        wake_time=-1;//default
        this.level = new Level(level,experience);
        this.calories=calories;
    }

    public void CalorieAcquisition(int minute, float Mets)
    {
        //칼로리 계산
        calories+=minute*0.0175*Mets*weight;
        // duration * 0.0175 * Mets * weight(kg)
            /*
               걷기 (느리게) - 3km/h 147 2.0
               걷기 (중간) - 5km/h 243 3.3
               걷기 (운동) - 5.5km/h 279 3.8
               자전거 (여유롭게) - <16km/h 294 4.0
               걷기(힘차게) - 6.5km/h 368 5.0
               스케이팅 - 16km/h 368 5.0
               자전거 (느린속도) - 18km/h 441 6.0
               달리기(조깅) - 8km/h 588 8.0
               달리기 - 10km/h 735 10.0
               자전거 (빠른속도) - 24km/h 735 10.0
               달리기 - 11km/h 845 11.5
               자전거 (매우 빠른속도) - 28km/h 882 12.0
               달리기 - 13km/h 992 13.5
               달리기 - 14.5km/h 1102 15.0
               달리기 - 16km/h 1176 16.0
            */
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

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public class Level {
        private int level;
        private int currentExperience;//현재경험치
        private int negativeExperience;//감소 경험치, 일정기간 이동하지 않다가 다시 이동하게 되면 증가됨.
        private long last_exercised; // 오랫동안 이동하지 않을 시 기록
        private int decrement_count; // 경고 알림, 일정 속도 이상의 이동 시에 초기화, 오랫동안 이동하지 않을 시 증가

        public Level(int level, int experience) {
            this.level=level;
            this.currentExperience=experience;
            negativeExperience=0;
            last_exercised=0;
            decrement_count=0;
        }

        public int getLevel(){
            return level;
        }
        public void setLevel(int level){
            this.level = level;
        }


        public int getCurrentExperience() {
            return currentExperience;
        }

        public void levelUp()
        {
            currentExperience-=getMaxExperience();
            level++;
        }

        public void dropExp()
        {
            negativeExperience += (int)(((System.currentTimeMillis() - last_exercised) / 3110400)/1000);
        }

        public void countDecrement()
        {
            decrement_count++;
        }

        public int getMaxExperience()
        {
            return (int)(Math.sqrt(level)*100);
        }

        public void ExpAcquisition(int exp)
        {
            //경험치 계산
            currentExperience += exp;
            // 성장도 =  칼로리 / 10 ( 성장 )
            // 비만도 =  negativeExperience ( 감소 )
            if(currentExperience < 0) currentExperience = 0;
        }//계산
    }
}
