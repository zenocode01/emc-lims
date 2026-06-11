import { useEffect, useState } from 'react'
import { Form, Input, InputNumber, Modal, Select, message } from 'antd'
import { sysUserApi, type SysUserVO, type SysUserDTO } from '../../../api/sys'
import { sysDeptApi, type SysDeptVO } from '../../../api/sys'

interface UserFormProps {
  open: boolean
  data?: SysUserVO
  deptOptions: { label: string; value: number }[]
  roleOptions: { label: string; value: number }[]
  onSuccess: () => void
  onCancel: () => void
}

export default function UserForm({
  open,
  data,
  deptOptions,
  roleOptions,
  onSuccess,
  onCancel,
}: UserFormProps) {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [allDeptOptions, setAllDeptOptions] = useState<{ label: string; value: number }[]>([])
  const [allRoleOptions, setAllRoleOptions] = useState<{ label: string; value: number }[]>([])

  // 加载部门和角色选项
  useEffect(() => {
    if (!open) return

    const loadData = async () => {
      try {
        // 获取部门列表（树形）
        const deptRes = await sysDeptApi.tree()
        const deptFlat = flattenTree(deptRes)
        setAllDeptOptions(deptFlat.map((d: SysDeptVO) => ({
          label: d.name,
          value: d.id!,
        })))

        // 获取角色列表
        const roleRes = await sysRoleApi.all()
        setAllRoleOptions(roleRes.map((r: any) => ({
          label: r.roleName,
          value: r.id,
        })))
      } catch {
        // error handled
      }
    }
    loadData()
  }, [open])

  // 扁平化部门树
  const flattenTree = (nodes: any[], prefix = ''): any[] => {
    let result: any[] = []
    for (const node of nodes) {
      result.push({ ...node, name: `${prefix}${node.name}` })
      if (node.children && node.children.length > 0) {
        result = result.concat(flattenTree(node.children, `${prefix}${node.name} - `))
      }
    }
    return result
  }

  // 加载角色 API
  const sysRoleApi = {
    all: () => {
      // 需要重新导入
    },
  }

  useEffect(() => {
    if (!open) return
    const load = async () => {
      try {
        const [deptRes, roleRes] = await Promise.all([
          sysDeptApi.tree(),
          request.get<any, any[]>('/sys/role/all'),
        ])
        const deptFlat = flattenTree(deptRes)
        setAllDeptOptions(deptFlat.map((d: any) => ({
          label: d.name,
          value: d.id!,
        })))
        setAllRoleOptions(roleRes.map((r: any) => ({
          label: r.roleName,
          value: r.id,
        })))
      } catch {
        // error handled
      }
    }
    load()
  }, [open])

  // 当表单打开时设置初始值
  useEffect(() => {
    if (open && data) {
      form.setFieldsValue({
        nickname: data.nickname,
        phone: data.phone,
        email: data.email,
        sex: data.sex,
        deptId: data.deptId,
        roleIds: data.roleId ? [data.roleId] : [],
        status: data.status,
        birthday: data.birthday,
        post: data.post,
        employeeCode: data.employeeCode,
      })
    } else if (open) {
      form.resetFields()
      form.setFieldsValue({
        status: 1,
        sex: 0,
      })
    }
  }, [open, data, form])

  const handleSubmit = async () => {
    const values = await form.validateFields()
    setLoading(true)
    try {
      const dto: SysUserDTO = {
        ...values,
        id: data?.id,
      }
      if (data?.id) {
        await sysUserApi.update(dto)
        message.success('更新成功')
      } else {
        await sysUserApi.create(dto)
        message.success('创建成功')
      }
      onSuccess()
    } catch {
      // error handled by interceptor
    } finally {
      setLoading(false)
    }
  }

  return (
    <Modal
      title={data ? '编辑用户' : '新增用户'}
      open={open}
      onOk={handleSubmit}
      onCancel={onCancel}
      confirmLoading={loading}
      width={600}
      destroyOnClose
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="username"
          label="用户名"
          rules={[{ required: !data, message: '请输入用户名' }]}
        >
          <Input placeholder="请输入用户名" disabled={!!data} />
        </Form.Item>

        <Form.Item
          name="password"
          label="密码"
          rules={[{ required: !data, message: '请输入密码' }]}
          extra={data ? '留空则不修改密码' : undefined}
        >
          <Input.Password placeholder={data ? '留空则不修改' : '请输入密码'} />
        </Form.Item>

        <Form.Item
          name="nickname"
          label="昵称"
          rules={[{ required: true, message: '请输入昵称' }]}
        >
          <Input placeholder="请输入昵称" />
        </Form.Item>

        <Form.Item
          name="phone"
          label="手机号"
          rules={[
            { required: true, message: '请输入手机号' },
            { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' },
          ]}
        >
          <Input placeholder="请输入手机号" />
        </Form.Item>

        <Form.Item name="email" label="邮箱">
          <Input placeholder="请输入邮箱" />
        </Form.Item>

        <Form.Item name="sex" label="性别" initialValue={0}>
          <Select>
            <Select.Option value={0}>未知</Select.Option>
            <Select.Option value={1}>男</Select.Option>
            <Select.Option value={2}>女</Select.Option>
          </Select>
        </Form.Item>

        <Form.Item name="deptId" label="所属部门">
          <Select
            options={allDeptOptions}
            placeholder="请选择部门"
            allowClear
          />
        </Form.Item>

        <Form.Item name="roleIds" label="角色">
          <Select
            mode="multiple"
            options={allRoleOptions}
            placeholder="请选择角色"
            allowClear
          />
        </Form.Item>

        <Form.Item name="status" label="状态" initialValue={1}>
          <Select>
            <Select.Option value={1}>启用</Select.Option>
            <Select.Option value={0}>禁用</Select.Option>
          </Select>
        </Form.Item>

        <Form.Item name="post" label="职位">
          <Input placeholder="请输入职位" />
        </Form.Item>

        <Form.Item name="employeeCode" label="工号">
          <Input placeholder="请输入工号" />
        </Form.Item>
      </Form>
    </Modal>
  )
}
