db.users.insertMany([
{"userName": "o_melnyk", "userId": "568545880"},
{"userName": "ruslansokol", "userId": "341631704"},
{"userName": "yehormonko", "userId": "201544707"},
{"userName": "lobin_eugene", "userId": "390509552"},
{"userName": "lowercasekitty", "userId": "236018370"},
{"userName": "oljk_o_o", "userId": "545636703"},
{"userName": "oberland", "userId": "218229593"},
{"userName": "bogdaninnova", "userId": "119970632"},
{"userName": "brnchnk", "userId": "185668152"},
{"userName": "mannaward", "userId": "25950253"},
{"userName": "mandarinaobshaetsia", "userId": "283147469"},
{"userName": "ildigrim", "userId": "193611353"},
])

{"userName": "Jormungandre", "userId": "283463865"},
db.users.find({userName: "Testing"})
db.users.find({name: "Tom"})
db.users.drop()

db.users.insertMany([
{"userName": "bogdaninnova", "userId": 119970632},
{"userName": "ildigrim", "userId": 193611353},
{"userName": "Jormungandre", "userId": 283463865},
])