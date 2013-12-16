#!/usr/bin/python
# coding=utf-8

from random import shuffle
from common import *

if len(argv) != 6:
    print_error("Usage: %s main.Class dbDir ensured.main.Class storageVariant directoriesVariant" % argv[0])
    exit(1)

STUDENT_CLAZZ = argv[1]
DB_DIR = argv[2]
ENSURED_CLAZZ = argv[3]
STORAGE_VARIANT = argv[4]
DIRECTORIES_VARIANT = argv[5]

ENSURED_JAVA_OPTIONS = '-Dfizteh.db.dir=%s -Dfizteh.db.file.format=%s -Dfizteh.db.dir.format=%s %s' \
                       % (DB_DIR, STORAGE_VARIANT, DIRECTORIES_VARIANT, ENSURED_CLAZZ)
STUDENT_JAVA_OPTIONS = '-Dfizteh.db.dir=%s %s' \
                       % (DB_DIR, STUDENT_CLAZZ)

keyZ = 'keyZ'
valueZ = hash_value(keyZ)
keyX = 'keyX'
valueX = hash_value(keyX) * 3


def create_map(size=10):
    m = dict()
    for i in range(size):
        key = 'key%d' % i
        m[key] = hash_value(key)
    m['ключ_на_русском_языке'] = 'значение_на_русском_языке'
    return m


MAP = create_map()

BIG_MAP = dict()
for i in range(10):
    key = '%skey' % i
    value = '%svalue' % 2 ** i
    BIG_MAP[key] = value

    key = hash_value('key%d')[:10]
    value = hash_value(key)
    BIG_MAP[key] = value

NAMES = {
    "Семен": "Храбрый",
    "Владимир": "Сильный",
    "Константин": "Красивый",
    "Никодим": "Добрый",
    "Евдоким": "Счастиливый",
    "Феофан": "ТаЕщеТварь",
    "Эклизиаст": "Милосердный",
    "Аполлон": "Женственный"
}
for key, value in NAMES.items():
    BIG_MAP[key] = value


def check_create_tables():
    shell = ExpectWrapper(STUDENT_JAVA_OPTIONS)

    shell.sendAndExpectAndGreet('put %s %s' % (keyZ, valueZ), 'no table')
    shell.sendAndExpectAndGreet('create default', 'created')
    shell.sendAndExpectAndGreet('create default', 'default exists')
    shell.sendAndExpectAndGreet('put %s %s' % (keyZ, valueZ), 'no table')
    shell.sendAndExpectAndGreet('create task4', 'created')
    shell.sendAndExpectAndGreet('put %s %s' % (keyZ, valueZ), 'no table')
    shell.sendAndExpectAndGreet('use task4', 'using task4')
    shell.sendAndExpectAndGreet('put %s %s' % (keyX, valueX), 'new')
    shell.sendAndExpectAndGreet('use foobar', 'foobar not exists')
    shell.sendAndExpectAndGreet('put %s %s' % (keyZ, valueZ), 'new')
    shell.sendAndExpectAndGreet('drop foobar', 'foobar not exists')

    shell.sendAndExpectAndGreet('use default', 'using default')
    shell.sendAndExpectAndGreet('drop default', 'dropped')
    shell.sendAndExpectAndGreet('put 1 1', 'no table')
    shell.sendAndExpectAndGreet('use default', 'default not exists')

    shell.sendExit()

    ensured_shell = ExpectWrapper(ENSURED_JAVA_OPTIONS)
    ensured_shell.sendAndExpectAndGreet('use default', 'default not exists')
    ensured_shell.sendAndExpectAndGreet('use task4', 'using task4')
    ensured_shell.sendAndExpectAndGreet('get %s' % keyX, valueX)

    ensured_shell.sendExit()


def check_fill_in():
    shell = ExpectWrapper(STUDENT_JAVA_OPTIONS)

    shell.sendAndExpectAndGreet('use task4', 'using task4')

    for key, value in BIG_MAP.items():
        shell.send('put %s %s' % (key, value))
        shell.expectGreet()

    shell.sendExit()


def check_persistence():
    shell = ExpectWrapper(STUDENT_JAVA_OPTIONS)

    items = BIG_MAP.items()[:]
    shuffle(items)
    items = items[:10]
    shell.sendAndExpectAndGreet('use task4', 'using task4')
    for key, value in items:
        shell.sendAndExpectAndGreet('get %s' % key, ('found', value))
        shell.sendAndExpectAndGreet('get %s' % value, 'not found')

    shell.sendExit()


def check_format_compatibility():
    ensured_shell = ExpectWrapper(ENSURED_JAVA_OPTIONS)

    ensured_shell.sendAndExpectAndGreet('use task4', 'using task4')

    items = BIG_MAP.items()[:]
    shuffle(items)
    items = items[:10]
    for key, value in items:
        ensured_shell.sendAndExpectAndGreet('get %s' % key, ('found', value))

    ensured_shell.sendExit()


to_remove = BIG_MAP.items()[:]
shuffle(to_remove)
to_remove = to_remove[:64]


def check_remove_consistency():
    shell = ExpectWrapper(STUDENT_JAVA_OPTIONS)

    shell.sendAndExpectAndGreet('use task4', 'using task4')

    for key, value in to_remove:
        shell.sendAndExpectAndGreet('remove %s' % key, 'removed')
        shell.sendAndExpectAndGreet('remove %s' % value, 'not found')

    shell.sendExit()

    ensured_shell = ExpectWrapper(ENSURED_JAVA_OPTIONS)

    ensured_shell.sendAndExpectAndGreet('use task4', 'using task4')

    for key, value in to_remove:
        ensured_shell.sendAndExpectAndGreet('get %s' % key, 'not found')

    items = BIG_MAP.items()[:]
    shuffle(items)
    items = items[:32]
    for key, value in items:
        if key not in to_remove:
            ensured_shell.sendAndExpectAndGreet('get %s' % key, 'found', value)

    ensured_shell.sendExit()


# exec_test(check_create_tables, 'Ошибка управления таблицами', True)
# exec_test(check_fill_in, 'Ошибка при наполнении базы', True)
# exec_test(check_persistence, 'Ошибка перзистентности', True)
# exec_test(check_format_compatibility, 'Ошибка совместимости форматов', True)
# exec_test(check_remove_consistency, 'Ошибка в удалении', True)
