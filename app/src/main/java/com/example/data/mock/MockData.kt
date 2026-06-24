package com.example.data.mock

import com.example.data.model.Course

object MockData {
    val courses = listOf(
        Course(
            id = "math_1",
            title = "Algebra Fundamentals",
            description = "Master the basics of linear equations, algebraic variables, and equations solving techniques. Essential for standard school level algebra exams.",
            category = "Mathematics",
            thumbnailUrl = "ic_launcher_foreground",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            transcript = """
                Welcome to Algebra Fundamentals. Today, we are going to explore the core language of algebra: variables, coefficients, and linear equations.
                
                At its heart, algebra is about finding the unknown. We use symbols—often letters like 'x' or 'y'—to stand for numbers we don't know yet. This is incredibly powerful because it allows us to write down general rules and solve tricky problems step-by-step.
                
                Let's start with a basic equation: 2x + 5 = 15.
                Here, 'x' is our variable. The number '2' multiplied by 'x' is called the coefficient. The number '5' is a constant.
                To find 'x', our goal is to isolate 'x' on one side of the equation.
                
                Step 1: Subtract 5 from both sides of the equation.
                2x + 5 - 5 = 15 - 5
                2x = 10
                
                Step 2: Divide both sides by the coefficient of x, which is 2.
                2x / 2 = 10 / 2
                x = 5
                
                Let's check our work! If we substitute x = 5 back into the original equation:
                2(5) + 5 = 10 + 5 = 15. It works!
                
                This process of performing the same operation on both sides of the equal sign is the key foundation of all algebra. As we move forward, we will deal with fractions, negative coefficients, and multiple variables. Keep practicing these basics, as they will make advanced subjects like calculus and physics much easier. Thank you for tuning in!
            """.trimIndent(),
            quizQuestionsJson = """
                [
                    {"question":"What is a letter that represents an unknown number in algebra called?","options":["Coefficient","Variable","Constant","Exponent"],"correctAnswerIndex":1},
                    {"question":"In the equation 3x + 7 = 22, what is the coefficient of x?","options":["3","7","22","x"],"correctAnswerIndex":0},
                    {"question":"Solve the equation: 5x - 4 = 16. What is x?","options":["2","3","4","5"],"correctAnswerIndex":2},
                    {"question":"What must you do to keep an equation balanced when modifying it?","options":["Perform the operation on only the left side","Perform the operation on only the right side","Perform the exact same operation on both sides","Divide both sides by zero"],"correctAnswerIndex":2},
                    {"question":"If x = 3, what is the value of 4x - 2?","options":["10","12","14","8"],"correctAnswerIndex":0}
                ]
            """.trimIndent()
        ),
        Course(
            id = "math_2",
            title = "Introduction to Geometry",
            description = "Explore angles, triangles, polygons, and the Pythagorean theorem with illustrative breakdowns of geometric proofs.",
            category = "Mathematics",
            thumbnailUrl = "ic_launcher_foreground",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            transcript = """
                Hello and welcome to Introduction to Geometry. Today, we will study the shapes that make up our physical world, focusing on lines, angles, and the famous Pythagorean Theorem.
                
                Geometry comes from Greek words meaning 'earth measurement'. We begin with the simplest concept: a point. A point has no size, only position. When we connect two points, we get a line.
                
                An angle is formed when two lines meet at a common endpoint. We measure angles in degrees. A right angle is exactly 90 degrees, forming a perfect square corner. Angles smaller than 90 degrees are acute, while angles larger than 90 degrees but smaller than 180 degrees are obtuse.
                
                Next, let's explore triangles. A triangle is a three-sided polygon whose interior angles always add up to exactly 180 degrees.
                
                One of the most useful rules in geometry is the Pythagorean Theorem, which applies ONLY to right-angled triangles. It states that the square of the longest side (the hypotenuse, 'c') is equal to the sum of the squares of the other two sides ('a' and 'b').
                
                Written mathematically: a² + b² = c²
                
                For example, if a right triangle has sides of length 3 cm and 4 cm:
                3² + 4² = 9 + 16 = 25.
                The square root of 25 is 5. So, the hypotenuse is exactly 5 cm!
                
                This simple equation is used daily by carpenters, engineers, and mapmakers to calculate distances and build square structures. Keep these definitions in mind, and let's jump into the quiz questions!
            """.trimIndent(),
            quizQuestionsJson = """
                [
                    {"question":"What is the sum of the interior angles of any triangle?","options":["90 degrees","180 degrees","270 degrees","360 degrees"],"correctAnswerIndex":1},
                    {"question":"What is an angle of exactly 90 degrees called?","options":["Acute angle","Obtuse angle","Right angle","Straight angle"],"correctAnswerIndex":2},
                    {"question":"What type of triangle does the Pythagorean Theorem apply to?","options":["Equilateral triangle","Isosceles triangle","Right-angled triangle","Scalene triangle"],"correctAnswerIndex":2},
                    {"question":"If a right triangle has sides of length 6 and 8, what is the length of its hypotenuse?","options":["10","12","14","9"],"correctAnswerIndex":0},
                    {"question":"What is the geometric term for a point where two or more lines meet?","options":["Vertex","Segment","Ray","Tangent"],"correctAnswerIndex":0}
                ]
            """.trimIndent()
        ),
        Course(
            id = "sci_1",
            title = "Basics of Photosynthesis",
            description = "Understand how plants harness sunlight, carbon dioxide, and water to produce oxygen and glucose.",
            category = "Science",
            thumbnailUrl = "ic_launcher_foreground",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
            transcript = """
                Welcome to Photosynthesis 101. Today, we look at the incredible biological engine that sustains almost all life on Earth.
                
                Photosynthesis is the process by which green plants, algae, and some bacteria convert light energy into chemical energy. In simple terms, plants use sunlight to make their own food!
                
                Where does this happen? Inside the plant cells, specifically in small structures called chloroplasts. Chloroplasts contain a green pigment called chlorophyll, which is highly efficient at capturing red and blue light waves from the sun.
                
                Let's break down the chemical recipe of photosynthesis. It requires three main ingredients:
                1. Water (H2O) absorbed by the roots from the soil.
                2. Carbon dioxide (CO2) absorbed from the air through microscopic pores in the leaves called stomata.
                3. Sunlight captured by chlorophyll.
                
                Using these inputs, the plant triggers a chemical reaction that rearranges these molecules into two final products:
                1. Glucose (C6H12O6), a simple sugar that the plant uses for energy and growth.
                2. Oxygen (O2), which the plant releases into the atmosphere as a byproduct.
                
                The complete chemical equation is:
                6CO₂ + 6H₂O + Light Energy ➔ C₆H₁₂O₆ + 6O₂
                
                Think about how incredible this is! Plants take a greenhouse gas (carbon dioxide) and water, and convert them into food for themselves and the very oxygen that we breathe. Without green plants performing photosynthesis, our atmosphere would lack oxygen, and the food chain would collapse. In the next section, we will test your understanding of this vital process.
            """.trimIndent(),
            quizQuestionsJson = """
                [
                    {"question":"Which pigment captures sunlight inside chloroplasts?","options":["Carotenoid","Chlorophyll","Melanin","Hemoglobin"],"correctAnswerIndex":1},
                    {"question":"What are the microscopic pores on plant leaves that absorb carbon dioxide called?","options":["Stomata","Pores","Roots","Chloroplasts"],"correctAnswerIndex":0},
                    {"question":"Which of the following is NOT required for photosynthesis?","options":["Sunlight","Water","Carbon Dioxide","Oxygen"],"correctAnswerIndex":3},
                    {"question":"What sugar molecule do plants produce as food during photosynthesis?","options":["Sucrose","Fructose","Glucose","Lactose"],"correctAnswerIndex":2},
                    {"question":"Where in the plant cell does photosynthesis take place?","options":["Nucleus","Mitochondria","Ribosome","Chloroplast"],"correctAnswerIndex":3}
                ]
            """.trimIndent()
        ),
        Course(
            id = "sci_2",
            title = "Understanding Gravity",
            description = "Learn about Isaac Newton's discovery of gravity, Albert Einstein's spacetime description, and how gravity governs our solar system.",
            category = "Science",
            thumbnailUrl = "ic_launcher_foreground",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
            transcript = """
                Welcome to our lesson on gravity. Today, we will discuss the invisible force that keeps our feet on the ground, controls the orbits of planets, and shapes the entire universe.
                
                Let's start with Sir Isaac Newton in the late 17th century. Legend has it that he saw an apple fall from a tree, which led him to ask: 'Why does the apple fall straight to the ground instead of floating away?'
                
                Newton proposed the Law of Universal Gravitation, which states that every object in the universe exerts an attractive pull on every other object. The strength of this pull depends on two things:
                1. Mass: Objects with more matter (like the Earth) have a stronger gravitational pull than objects with less matter (like an apple).
                2. Distance: The closer two objects are, the stronger the pull. If you move them further apart, the gravity weakens.
                
                Later, in 1915, Albert Einstein expanded our understanding of gravity with his Theory of General Relativity. He explained that gravity isn't just an attractive pull—it's actually the bending of space and time.
                
                Imagine space as a stretched rubber sheet. If you place a heavy bowling ball in the middle, it creates a deep dip. If you roll a small marble across the sheet, it will roll around the bowling ball because of the curved surface. In this analogy, the bowling ball is the Sun, and the marble is Earth!
                
                Gravity is what keeps the Moon in orbit around Earth, holds our atmosphere in place, and prevents oceans from drifting into space. Let's start the quiz and see how well you've understood the laws of physics.
            """.trimIndent(),
            quizQuestionsJson = """
                [
                    {"question":"Who proposed the Law of Universal Gravitation after observing a falling apple?","options":["Albert Einstein","Galileo Galilei","Isaac Newton","Nikola Tesla"],"correctAnswerIndex":2},
                    {"question":"What are the two factors that determine the strength of gravity?","options":["Mass and Distance","Speed and Temperature","Volume and Mass","Color and Altitude"],"correctAnswerIndex":0},
                    {"question":"According to Einstein, what is gravity?","options":["An invisible magnet","The bending of space and time","A chemical reaction","Atmospheric pressure"],"correctAnswerIndex":1},
                    {"question":"What celestial body holds the Earth in orbit using its immense gravity?","options":["The Moon","Jupiter","The Sun","Mars"],"correctAnswerIndex":2},
                    {"question":"If you go to the Moon, how does your mass change compared to Earth?","options":["It decreases","It increases","It stays the exact same","It becomes zero"],"correctAnswerIndex":2}
                ]
            """.trimIndent()
        ),
        Course(
            id = "eng_1",
            title = "English Grammar: Tenses",
            description = "A comprehensive guide to Past, Present, and Future tenses, with common verbs and sentence construction rules.",
            category = "English",
            thumbnailUrl = "ic_launcher_foreground",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
            transcript = """
                Hello, English learners! Welcome to English Grammar: Tenses. Understanding tenses is the key to expressing time in English clearly and confidently.
                
                There are three primary time frames in English:
                1. The Present (actions happening right now or habitually).
                2. The Past (actions that already happened).
                3. The Future (actions that will happen).
                
                Within each of these time frames, we have four main aspects: Simple, Continuous, Perfect, and Perfect Continuous. Today, we will focus on the simple and continuous aspects.
                
                Let's start with the Simple Present: 'I eat rice.'
                This describes a general habit or truth. If we change it to Present Continuous: 'I am eating rice,' it means the action is happening right this very second. Notice how we add 'am' (helping verb) and '-ing' to the main verb.
                
                Now let's look at the Simple Past. For regular verbs, we usually add '-ed' to the end: 'I walked to school yesterday.'
                For irregular verbs, the spelling changes entirely: 'I ate rice.' (Past of 'eat').
                
                Finally, the Simple Future. This is easy! We simply add the helping verb 'will' before the base verb: 'I will eat rice tomorrow.'
                
                Understanding these structures allows you to tell stories, write essays, and hold fluent conversations. Practice converting sentences between past, present, and future. Now, let's take a look at our grammar quiz!
            """.trimIndent(),
            quizQuestionsJson = """
                [
                    {"question":"Which sentence is in the Present Continuous tense?","options":["I play football.","I played football.","I am playing football.","I will play football."],"correctAnswerIndex":2},
                    {"question":"What is the past tense of the irregular verb 'write'?","options":["Writed","Wrote","Written","Writing"],"correctAnswerIndex":1},
                    {"question":"Complete the sentence: 'Tomorrow, she ______ to New Delhi.'","options":["goes","went","will go","is going to"],"correctAnswerIndex":2},
                    {"question":"Which tense is used to describe a general habit or universal truth?","options":["Simple Present","Simple Past","Present Perfect","Simple Future"],"correctAnswerIndex":0},
                    {"question":"Choose the correct past tense sentence:","options":["He did runned very fast.","He runned very fast.","He ran very fast.","He will run very fast."],"correctAnswerIndex":2}
                ]
            """.trimIndent()
        ),
        Course(
            id = "eng_2",
            title = "Conversational English Basics",
            description = "Learn how to introduce yourself, ask questions, give directions, and hold polite daily conversations in English.",
            category = "English",
            thumbnailUrl = "ic_launcher_foreground",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4",
            transcript = """
                Welcome to Conversational English Basics. Learning English isn't just about reading textbooks—it's about connecting with other people in daily life.
                
                Today, we will learn three simple scenarios: introducing yourself, asking polite questions, and greeting someone.
                
                Scenario 1: Introductions.
                When you meet someone for the first time, a warm greeting is essential. You can say:
                'Hello! My name is Rahul. Nice to meet you.'
                The other person will usually reply: 'Nice to meet you too, Rahul! I am Priya.'
                To keep the conversation going, you can ask simple questions like: 'How are you today?' or 'Where are you from?'
                
                Scenario 2: Asking for help or directions.
                Politeness is key in English. Always start with 'Excuse me' when seeking someone's attention.
                For example: 'Excuse me, could you tell me where the library is?'
                Or: 'Excuse me, do you know what time it is?'
                Using 'please' and 'thank you' goes a long way. When someone helps you, always say: 'Thank you so much for your help!'
                
                Scenario 3: Saying goodbye.
                When leaving, polite phrases include:
                'It was great talking to you. Have a nice day!' or 'See you later!'
                
                By practicing these simple, natural templates, you will build confidence to speak English fluently in schools, jobs, and travel. Let's proceed to the interactive conversational quiz.
            """.trimIndent(),
            quizQuestionsJson = """
                [
                    {"question":"Which is the most polite way to get someone's attention?","options":["Hey you!","Excuse me","Tell me something","What is your name?"],"correctAnswerIndex":1},
                    {"question":"How should you respond when someone says, 'Nice to meet you'?","options":["Yes, I know.","Nice to meet you too.","Thank you.","Goodbye."],"correctAnswerIndex":1},
                    {"question":"What should you say after someone helps you find your way?","options":["You are welcome.","No problem.","Thank you so much for your help!","I am sorry."],"correctAnswerIndex":2},
                    {"question":"Which question is best to keep a conversation going?","options":["What is your weight?","How are you doing today?","Who are you?","Do you have money?"],"correctAnswerIndex":1},
                    {"question":"Which phrase is a common, friendly way to say goodbye?","options":["See you later!","Go away.","Stay here.","Hello."],"correctAnswerIndex":0}
                ]
            """.trimIndent()
        ),
        Course(
            id = "soc_1",
            title = "India's Independence Struggle",
            description = "A historical review of India's journey to freedom from British colonial rule, highlighting Gandhi, Bose, and the partition.",
            category = "Social Studies",
            thumbnailUrl = "ic_launcher_foreground",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
            transcript = """
                Welcome to our class on India's Independence Struggle. Today, we pay tribute to the millions of brave men and women who fought to secure the freedom we enjoy today.
                
                For nearly 200 years, India was under the rule of the British Empire, managed initially by the East India Company and later directly by the British Crown. The struggle for freedom was not a single event, but a long, complex movement spanning generations.
                
                A major turning point occurred in 1915 when Mohandas Karamchand Gandhi returned to India from South Africa. He introduced a unique philosophy of struggle called Satyagraha—resistance through non-violent civil disobedience. He led major nationwide movements, including the Non-Cooperation Movement (1920), the Salt March (1930), and the Quit India Movement (1942).
                
                Alongside Mahatma Gandhi, many other legendary leaders contributed. Netaji Subhash Chandra Bose believed in a more active, military stance and formed the Indian National Army (INA) to fight the British. Bhagat Singh, Chandrashekhar Azad, and Sukhdev inspired millions of youth with their supreme sacrifice.
                
                After years of struggle, strikes, and global political shifts following World War II, the British Parliament finally passed the Indian Independence Act. On August 15, 1947, India became an independent nation, with Pandit Jawaharlal Nehru as its first Prime Minister.
                
                Nehru's famous speech, 'Tryst with Destiny', marked the birth of a sovereign, democratic republic. Let's start the quiz and test our historical knowledge of this monumental movement.
            """.trimIndent(),
            quizQuestionsJson = """
                [
                    {"question":"In which year did India gain Independence from British rule?","options":["1942","1945","1947","1950"],"correctAnswerIndex":2},
                    {"question":"Who was the first Prime Minister of independent India?","options":["Mahatma Gandhi","Jawaharlal Nehru","Sardar Patel","Dr. B.R. Ambedkar"],"correctAnswerIndex":1},
                    {"question":"What philosophy of non-violent resistance did Mahatma Gandhi introduce?","options":["Satyagraha","Swaraj","Inquilab","Ahimsa"],"correctAnswerIndex":0},
                    {"question":"Who founded the Indian National Army (INA)?","options":["Bhagat Singh","Subhash Chandra Bose","Bal Gangadhar Tilak","Lala Lajpat Rai"],"correctAnswerIndex":1},
                    {"question":"Which major movement was launched by Gandhi in 1942, demanding British exit?","options":["Salt March","Non-Cooperation Movement","Quit India Movement","Swadeshi Movement"],"correctAnswerIndex":2}
                ]
            """.trimIndent()
        ),
        Course(
            id = "soc_2",
            title = "Our Solar System & Earth",
            description = "Discover the planets orbiting our Sun, the structure of our atmosphere, and what makes Earth unique for supporting life.",
            category = "Social Studies",
            thumbnailUrl = "ic_launcher_foreground",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4",
            transcript = """
                Welcome, astronomers! Today we are leaving the ground to explore Our Solar System and our home planet, Earth.
                
                Our Solar System is located in the Milky Way galaxy. It consists of our star—the Sun—and everything bound to it by gravity: eight major planets, their moons, dwarf planets, and asteroids.
                
                The Sun contains 99.8% of the total mass of the solar system, making it the supreme gravitational center.
                
                The planets are divided into two groups:
                1. The Terrestrial (Rocky) Planets: Mercury, Venus, Earth, and Mars. They are closer to the Sun and have solid surfaces.
                2. The Gas Giants: Jupiter, Saturn, Uranus, and Neptune. They are massive spheres made mostly of hydrogen, helium, and ice.
                
                Earth is the third planet from the Sun and the ONLY celestial body known to support life. What makes Earth so special?
                1. Liquid Water: Earth is in the 'Goldilocks Zone'—not too close to the Sun (where water would boil away) and not too far (where it would freeze solid).
                2. Atmosphere: Earth's atmosphere is rich in oxygen (21%) and nitrogen (78%), shielding us from solar radiation and keeping temperatures stable.
                3. Magnetic Field: Created by its spinning iron-nickel core, Earth's magnetic field deflects dangerous solar winds.
                
                Studying our neighborhood in space helps us understand climate change, physics, and how precious our fragile blue planet really is. Let's launch into the final quiz and see what you've learned!
            """.trimIndent(),
            quizQuestionsJson = """
                [
                    {"question":"Which planet is known as the 'Red Planet'?","options":["Venus","Mars","Jupiter","Saturn"],"correctAnswerIndex":1},
                    {"question":"What gas makes up the majority of Earth's atmosphere?","options":["Oxygen","Carbon Dioxide","Nitrogen","Hydrogen"],"correctAnswerIndex":2},
                    {"question":"Why is Earth's position called the 'Goldilocks Zone'?","options":["It is close to the moon","It has a golden appearance","It is at the perfect distance from the Sun to support liquid water","It is covered in forests"],"correctAnswerIndex":2},
                    {"question":"Which is the largest planet in our solar system?","options":["Saturn","Jupiter","Neptune","Uranus"],"correctAnswerIndex":1},
                    {"question":"What keeps all the planets orbiting the Sun instead of drifting away?","options":["Magnetic force","Solar winds","The Sun's gravity","Atmospheric pressure"],"correctAnswerIndex":2}
                ]
            """.trimIndent()
        )
    )
}
